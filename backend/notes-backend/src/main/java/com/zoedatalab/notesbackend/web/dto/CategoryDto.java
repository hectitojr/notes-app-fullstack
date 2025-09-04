package com.zoedatalab.notesbackend.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(Long id, @NotBlank String name) {}
