package org.test.calendar.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors
) {
    
    public record FieldError(
            String field,
            Object rejectedValue,
            String message
    ) {}
}

