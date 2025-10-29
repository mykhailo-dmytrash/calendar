package org.test.calendar.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "Pagination request parameters")
public record PaginationRequest(
        @Schema(description = "Page number (0-based)", example = "0", minimum = "0")
        @Min(value = 0, message = "Page number must be non-negative")
        int page,
        
        @Schema(description = "Number of items per page", example = "10", minimum = "1", maximum = "100")
        @Min(value = 1, message = "Size must be at least 1")
        @Max(value = 100, message = "Size must not exceed 100")
        int size
) {
}
