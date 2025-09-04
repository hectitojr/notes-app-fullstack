package com.zoedatalab.notesbackend.web.controller;

import com.zoedatalab.notesbackend.service.NoteService;
import com.zoedatalab.notesbackend.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {
    private final NoteService service;
    public NoteController(NoteService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<NoteDto> create(@RequestBody @Valid NoteCreateRequest req) {
        NoteDto created = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1/notes/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public NoteDto get(@PathVariable Long id) { return service.get(id); }

    @GetMapping
    public Page<NoteDto> list(@RequestParam(defaultValue = "false") boolean archived,
                              @RequestParam(required = false) String category,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        return service.list(archived, category, PageRequest.of(page, size));
    }

    @PutMapping("/{id}")
    public NoteDto update(@PathVariable Long id, @RequestBody @Valid NoteUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/archive")
    public NoteDto archive(@PathVariable Long id) { return service.archive(id, true); }

    @PostMapping("/{id}/unarchive")
    public NoteDto unarchive(@PathVariable Long id) { return service.archive(id, false); }

    @PostMapping("/{id}/categories/{name}")
    public NoteDto addCategory(@PathVariable Long id, @PathVariable String name) {
        return service.addCategory(id, name);
    }

    @DeleteMapping("/{id}/categories/{name}")
    public NoteDto removeCategory(@PathVariable Long id, @PathVariable String name) {
        return service.removeCategory(id, name);
    }
}
