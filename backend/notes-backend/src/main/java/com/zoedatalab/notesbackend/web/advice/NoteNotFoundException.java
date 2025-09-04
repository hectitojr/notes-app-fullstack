package com.zoedatalab.notesbackend.web.advice;

public class NoteNotFoundException extends DomainException {
    public NoteNotFoundException(Long id) {
        super(ErrorCode.NOTE_NOT_FOUND, "La nota con id %d no existe".formatted(id));
    }
}
