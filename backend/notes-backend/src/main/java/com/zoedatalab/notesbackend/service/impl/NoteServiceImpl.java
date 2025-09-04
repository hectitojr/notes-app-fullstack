package com.zoedatalab.notesbackend.service.impl;

import com.zoedatalab.notesbackend.domain.Category;
import com.zoedatalab.notesbackend.domain.Note;
import com.zoedatalab.notesbackend.repository.CategoryRepository;
import com.zoedatalab.notesbackend.repository.NoteRepository;
import com.zoedatalab.notesbackend.service.CategoryService;
import com.zoedatalab.notesbackend.service.NoteService;
import com.zoedatalab.notesbackend.web.advice.BadRequestDomainException;
import com.zoedatalab.notesbackend.web.advice.ErrorCode;
import com.zoedatalab.notesbackend.web.advice.NoteNotFoundException;
import com.zoedatalab.notesbackend.web.dto.*;
import com.zoedatalab.notesbackend.web.mapper.NoteMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository notes;
    private final CategoryRepository categories;
    private final CategoryService categoryService; // helper getOrCreate
    private final NoteMapper mapper;

    public NoteServiceImpl(
            NoteRepository notes,
            CategoryRepository categories,
            CategoryService categoryService,
            NoteMapper mapper
    ) {
        this.notes = notes;
        this.categories = categories;
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @Override
    public NoteDto create(NoteCreateRequest req) {
        String title = req.title() == null ? "" : req.title().trim();
        if (title.isEmpty()) {
            throw new BadRequestDomainException(ErrorCode.INVALID_NOTE_TITLE,
                    "El título de la nota es obligatorio");
        }

        Note n = new Note();
        n.setTitle(title);
        n.setContent(req.content() == null ? "" : req.content().trim());

        if (req.categories() != null && !req.categories().isEmpty()) {
            Set<Category> cats = req.categories().stream()
                    .map(s -> s == null ? "" : s.trim())
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .distinct()
                    .limit(10)
                    .map(categoryService::getOrCreateByName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            n.getCategories().addAll(cats);
        }
        return mapper.toDto(notes.save(n));
    }

    @Override
    @Transactional(readOnly = true)
    public NoteDto get(Long id) {
        Note n = notes.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        return mapper.toDto(n);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoteDto> list(boolean archived, String category, Pageable pageable) {
        Page<Note> page = (category == null || category.isBlank())
                ? notes.findByArchived(archived, pageable)
                : notes.findByArchivedAndCategories_NameIgnoreCase(
                archived, category.trim(), pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public NoteDto update(Long id, NoteUpdateRequest req) {
        Note n = notes.findById(id).orElseThrow(() -> new NoteNotFoundException(id));

        String title = req.title() == null ? "" : req.title().trim();
        if (title.isEmpty()) {
            throw new BadRequestDomainException(ErrorCode.INVALID_NOTE_TITLE,
                    "El título de la nota no puede ser vacío");
        }
        n.setTitle(title);

        if (req.content() != null) {
            n.setContent(req.content().trim());
        }
        if (req.archived() != null) {
            n.setArchived(req.archived());
        }
        return mapper.toDto(n);
    }

    @Override
    public void delete(Long id) {
        if (!notes.existsById(id)) {
            throw new NoteNotFoundException(id);
        }
        notes.deleteById(id);
    }

    @Override
    public NoteDto addCategory(Long noteId, String categoryName) {
        Note n = notes.findById(noteId).orElseThrow(() -> new NoteNotFoundException(noteId));

        String normalized = categoryName == null ? "" : categoryName.trim();
        if (normalized.isEmpty()) {
            throw new BadRequestDomainException(ErrorCode.INVALID_CATEGORY_NAME,
                    "El nombre de la categoría es obligatorio");
        }
        Category c = categoryService.getOrCreateByName(normalized);
        n.getCategories().add(c);
        return mapper.toDto(n);
    }

    @Override
    public NoteDto removeCategory(Long noteId, String categoryName) {
        Note n = notes.findById(noteId).orElseThrow(() -> new NoteNotFoundException(noteId));

        String normalized = categoryName == null ? "" : categoryName.trim();
        if (normalized.isEmpty()) {
            throw new BadRequestDomainException(ErrorCode.INVALID_CATEGORY_NAME,
                    "El nombre de la categoría es obligatorio");
        }
        categories.findByNameIgnoreCase(normalized)
                .ifPresent(n.getCategories()::remove);
        return mapper.toDto(n);
    }

    @Override
    public NoteDto archive(Long id, boolean archived) {
        Note n = notes.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        n.setArchived(archived);
        return mapper.toDto(n);
    }
}
