package org.test.calendar.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated response containing events and pagination metadata")
public record PaginatedResponse<T>(
        @Schema(description = "List of elements for the current page")
        List<T> content,
        
        @Schema(description = "Current page number (0-based)", example = "0")
        int page,
        
        @Schema(description = "Number of items per page", example = "10")
        int size,
        
        @Schema(description = "Total number of elements across all pages", example = "25")
        long totalElements,
        
        @Schema(description = "Total number of pages", example = "3")
        int totalPages,
        
        @Schema(description = "Whether this is the first page", example = "true")
        boolean first,
        
        @Schema(description = "Whether this is the last page", example = "false")
        boolean last
) {
}
