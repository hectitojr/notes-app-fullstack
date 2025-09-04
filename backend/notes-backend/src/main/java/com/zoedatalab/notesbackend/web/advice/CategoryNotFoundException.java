package com.zoedatalab.notesbackend.web.advice;

public class CategoryNotFoundException extends DomainException {
    public CategoryNotFoundException(String name) {
        super(ErrorCode.CATEGORY_NOT_FOUND, "La categoría '%s' no existe".formatted(name));
    }
}
