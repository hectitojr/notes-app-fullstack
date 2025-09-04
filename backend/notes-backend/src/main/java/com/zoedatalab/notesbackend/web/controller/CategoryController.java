package com.zoedatalab.notesbackend.web.controller;

import com.zoedatalab.notesbackend.service.CategoryService;
import com.zoedatalab.notesbackend.web.dto.CategoryCreateRequest;
import com.zoedatalab.notesbackend.web.dto.CategoryDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid CategoryCreateRequest req) {
        CategoryDto created = service.create(req.name());
        return ResponseEntity.created(URI.create("/api/v1/categories/" + created.id())).body(created);
    }

    @GetMapping
    public List<CategoryDto> list() { return service.list(); }
}
