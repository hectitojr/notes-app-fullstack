package com.zoedatalab.notesbackend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        @Size(max = 50, message = "El nombre de la categoría no debe exceder 50 caracteres")
        String name
) {}
