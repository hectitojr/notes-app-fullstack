package com.zoedatalab.notesbackend.web.advice;

public abstract class DomainException extends RuntimeException {
    private final ErrorCode code;

    protected DomainException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode code() { return code; }
}