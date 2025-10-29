package org.test.calendar.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Represents a preview of a calendar event with essential details")
public record EventPreviewDto(

        @Schema(
            description = "Unique identifier for the event",
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,

        @Schema(description = "Title of the event", example = "Team Sync")
        String title,

        @Schema(description = "Start date and time of the event in ISO-8601 format with timezone", example = "2025-10-23T09:30:00+03:00[Europe/Kyiv]")
        ZonedDateTime startAt,

        @Schema(description = "End date and time of the event in ISO-8601 format with timezone", example = "2025-10-23T10:00:00+03:00[Europe/Kyiv]")
        ZonedDateTime finishAt,

        @Schema(description = "Location of the event", example = "Conference Room A", nullable = true)
        String location
) {
}
