package org.test.calendar.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.test.calendar.validation.EventValidation;
import org.test.calendar.validation.annotation.SameDay;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Represents a calendar event with all necessary details")
@SameDay(fromField = "startAt", toField = "finishAt", groups = EventValidation.EventCreation.class)
public record EventDto(

        @Schema(
            description = "Unique identifier for the event",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY
        )
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        UUID id,

        @Schema(description = "Title of the event", example = "Team Sync")
        @NotBlank(groups = EventValidation.EventCreation.class)
        @Pattern(regexp = "^[A-Za-z0-9\\s\\-@]+$", message = "Title must contain only alphanumeric characters, spaces, hyphens, and @ symbols", groups = EventValidation.EventCreation.class)
        String title,

        @Schema(description = "Description of the event", example = "Morning")
        @NotBlank(groups = EventValidation.EventCreation.class)
        @Pattern(regexp = "^[A-Za-z0-9\\s\\-@]+$", message = "Description must contain only alphanumeric characters, spaces, hyphens, and @ symbols", groups = EventValidation.EventCreation.class)
        String description,

        @Schema(description = "Start date and time of the event in ISO-8601 format with timezone", example = "2025-10-23T09:30:00+03:00[Europe/Kyiv]")
        @NotNull(groups = EventValidation.EventCreation.class)
        ZonedDateTime startAt,

        @Schema(description = "End date and time of the event in ISO-8601 format with timezone", example = "2025-10-23T10:00:00+03:00[Europe/Kyiv]")
        @NotNull(groups = EventValidation.EventCreation.class)
        ZonedDateTime finishAt,

        @Schema(description = "Location of the event", example = "Conference Room A", nullable = true)
        @Pattern(regexp = "^[A-Za-z0-9\\s\\-@]+$", message = "Location must contain only alphanumeric characters, spaces, hyphens, and @ symbols", groups = EventValidation.EventCreation.class)
        String location
) {
}
