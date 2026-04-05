package com.taskapp.taskservice.exception;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorInfo> errors
) {
    public static String nowIso8601() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }
}
