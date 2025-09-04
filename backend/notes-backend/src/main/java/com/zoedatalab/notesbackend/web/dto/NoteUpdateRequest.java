package com.zoedatalab.notesbackend.web.dto;

import jakarta.validation.constraints.NotBlank;

public record NoteUpdateRequest(
        @NotBlank(message = "El título de la nota no puede ser vacío")
        String title,
        String content,
        Boolean archived
) {}