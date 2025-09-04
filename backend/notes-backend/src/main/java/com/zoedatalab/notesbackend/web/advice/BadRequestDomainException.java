package com.zoedatalab.notesbackend.web.advice;

public class BadRequestDomainException extends DomainException {
    public BadRequestDomainException(ErrorCode code, String message) {
        super(code, message);
    }
}
