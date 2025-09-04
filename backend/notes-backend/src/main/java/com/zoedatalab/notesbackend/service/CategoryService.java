package com.zoedatalab.notesbackend.service;

import com.zoedatalab.notesbackend.domain.Category;
import com.zoedatalab.notesbackend.web.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(String name);
    List<CategoryDto> list();

    /**
     * Obtiene una categoría por nombre (normalizando) o la crea si no existe.
     * Idempotente: no crea duplicados por mayúsculas/minúsculas o espacios.
     */
    Category getOrCreateByName(String name);
}
