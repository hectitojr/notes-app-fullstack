package com.zoedatalab.notesbackend.service.impl;

import com.zoedatalab.notesbackend.domain.Category;
import com.zoedatalab.notesbackend.repository.CategoryRepository;
import com.zoedatalab.notesbackend.service.CategoryService;
import com.zoedatalab.notesbackend.web.advice.BadRequestDomainException;
import com.zoedatalab.notesbackend.web.advice.ErrorCode;
import com.zoedatalab.notesbackend.web.dto.CategoryDto;
import com.zoedatalab.notesbackend.web.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryRepository repo, CategoryMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CategoryDto create(String name) {
        String normalized = normalize(name);
        if (normalized.isEmpty()) {
            // Por si llegara vacío (aunque el DTO ya valida @NotBlank)
            throw new BadRequestDomainException(ErrorCode.INVALID_CATEGORY_NAME,
                    "El nombre de la categoría es obligatorio");
        }
        var existing = repo.findByNameIgnoreCase(normalized).orElse(null);
        if (existing != null) {
            return mapper.toDto(existing);
        }
        Category c = new Category();
        c.setName(normalized); // persistimos en minúsculas para consistencia
        return mapper.toDto(repo.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> list() {
        return repo.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public Category getOrCreateByName(String name) {
        String normalized = normalize(name);
        if (normalized.isEmpty()) {
            throw new BadRequestDomainException(ErrorCode.INVALID_CATEGORY_NAME,
                    "El nombre de la categoría es obligatorio");
        }
        return repo.findByNameIgnoreCase(normalized).orElseGet(() -> {
            Category c = new Category();
            c.setName(normalized);
            return repo.save(c);
        });
    }

    private static String normalize(String s) {
        // trim + lowercase para uniformidad e idempotencia
        return s == null ? "" : s.trim().toLowerCase();
    }
}
