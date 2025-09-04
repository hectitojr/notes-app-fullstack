package com.zoedatalab.notesbackend.web.advice;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldError> errors
) {
    public static ApiError of(int status, ErrorCode code, String message, String path, List<FieldError> errors) {
        return new ApiError(Instant.now(), status, code.name(), message, path, errors);
    }

    public record FieldError(String field, String message) {}
}
