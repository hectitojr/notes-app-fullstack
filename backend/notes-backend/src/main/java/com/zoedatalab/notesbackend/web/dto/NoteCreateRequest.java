package com.zoedatalab.notesbackend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record NoteCreateRequest(
        @NotBlank(message = "El título de la nota es obligatorio")
        String title,
        String content,
        @Size(max = 10, message = "Como máximo puedes enviar 10 categorías")
        Set<@NotBlank(message = "Cada categoría debe tener nombre") String> categories
) {}