package org.test.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.test.calendar.model.dto.EventDto;
import org.test.calendar.model.dto.EventPreviewDto;
import org.test.calendar.model.dto.PaginatedResponse;
import org.test.calendar.service.EventService;
import org.test.calendar.validation.EventValidation;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "APIs for managing calendar events")
public class EventController {

    private final EventService eventService;

    @Operation(
        summary = "Create a new event",
        description = "Creates a new calendar event with the provided details. The start and finish times must be on the same day and start must be before finish."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Event created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EventDto.class)
            )
        )
    })
    @PostMapping
    public EventDto createEvent(
        @Parameter(
            description = "Event details including title, description, start and finish times",
            required = true
        )
        @Validated(EventValidation.EventCreation.class) 
        @RequestBody EventDto eventDto
    ) {
        log.debug("Creating event {}", eventDto);
        return eventService.createEvent(eventDto);
    }

    @Operation(
        summary = "Delete an event",
        description = "Deletes an existing calendar event by its unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Event deleted successfully"
        )
    })
    @DeleteMapping("/{id}")
    public void deleteEvent(
        @Parameter(
            description = "Unique identifier of the event to delete",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable("id") UUID id
    ) {
        log.debug("Deleting event with id {}", id);
        eventService.deleteEvent(id);
    }

    @Operation(
        summary = "Get event by ID",
        description = "Retrieves a specific calendar event by its unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Event found and returned successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EventDto.class)
            )
        )
    })
    @GetMapping("/{id}")
    public EventDto getEventById(
        @Parameter(
            description = "Unique identifier of the event to retrieve",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable("id") UUID id
    ) {
        log.debug("Getting event with id {}", id);
        return eventService.getEventById(id);
    }

    @Operation(
        summary = "Update an existing event",
        description = "Updates an existing calendar event with new details. The start and finish times must be on the same day and start must be before finish."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Event updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EventDto.class)
            )
        )
    })
    @PutMapping("/{id}")
    public EventDto updateEvent(
        @Parameter(
            description = "Unique identifier of the event to update",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable("id") UUID id,
        @Parameter(
            description = "Updated event details including title, description, start and finish times",
            required = true
        )
        @Validated(EventValidation.EventUpdating.class) 
        @RequestBody EventDto eventDto
    ) {
        log.debug("Updating event with id {}", id);
        return eventService.updateEvent(id, eventDto);
    }

    @Operation(
        summary = "Get event previews for a specific month",
        description = "Retrieves a list of event previews (id, title, start time) for events in the specified month. The date parameter can be any date within the desired month; the timezone from this date will be used for calculations."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Event previews retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EventPreviewDto.class)
            )
        )
    })
    @GetMapping("/previews/month")
    public List<EventPreviewDto> getEventPreviewsForMonth(
        @Parameter(
            description = "Any date within the desired month in ISO-8601 format with timezone (e.g., '2025-10-15T00:00:00+03:00[Europe/Kyiv]')",
            required = true,
            example = "2025-10-15T00:00:00+03:00[Europe/Kyiv]"
        )
        @RequestParam ZonedDateTime date
    ) {
        log.debug("Getting event previews for date: {}", date);
        return eventService.getEventPreviewsForMonth(date);
    }
}
