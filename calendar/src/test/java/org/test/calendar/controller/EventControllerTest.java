package org.test.calendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.test.calendar.exception.BusinessLogicException;
import org.test.calendar.model.dto.EventDto;
import org.test.calendar.model.dto.EventPreviewDto;
import org.test.calendar.service.EventService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @Test
    void createEvent_shouldReturnCreatedEvent() throws Exception {
        ZonedDateTime startAt = ZonedDateTime.parse("2025-10-23T09:30:00+03:00[Europe/Kyiv]");
        ZonedDateTime finishAt = ZonedDateTime.parse("2025-10-23T10:00:00+03:00[Europe/Kyiv]");
        UUID eventId = UUID.randomUUID();

        EventDto inputDto = new EventDto(null, "Team Meeting", "Daily standup", startAt, finishAt, "Room A");
        EventDto outputDto = new EventDto(eventId, "Team Meeting", "Daily standup", startAt, finishAt, "Room A");

        when(eventService.createEvent(any(EventDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.title").value("Team Meeting"))
                .andExpect(jsonPath("$.description").value("Daily standup"))
                .andExpect(jsonPath("$.location").value("Room A"));

        verify(eventService).createEvent(any(EventDto.class));
    }

    @Test
    void deleteEvent_shouldCallServiceAndReturnOk() throws Exception {
        UUID eventId = UUID.randomUUID();

        doNothing().when(eventService).deleteEvent(eventId);

        mockMvc.perform(delete("/events/{id}", eventId))
                .andExpect(status().isOk());

        verify(eventService).deleteEvent(eventId);
    }

    @Test
    void getEventById_whenEventExists_shouldReturnEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        ZonedDateTime startAt = ZonedDateTime.parse("2025-10-23T09:30:00+03:00[Europe/Kyiv]");
        ZonedDateTime finishAt = ZonedDateTime.parse("2025-10-23T10:00:00+03:00[Europe/Kyiv]");

        EventDto eventDto = new EventDto(eventId, "Team Meeting", "Daily standup", startAt, finishAt, "Room A");

        when(eventService.getEventById(eventId)).thenReturn(eventDto);

        mockMvc.perform(get("/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.title").value("Team Meeting"))
                .andExpect(jsonPath("$.description").value("Daily standup"))
                .andExpect(jsonPath("$.location").value("Room A"));

        verify(eventService).getEventById(eventId);
    }

    @Test
    void getEventById_whenEventDoesNotExist_shouldReturnError() throws Exception {
        UUID eventId = UUID.randomUUID();

        when(eventService.getEventById(eventId))
                .thenThrow(new BusinessLogicException("Event not found with id: " + eventId));

        mockMvc.perform(get("/events/{id}", eventId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(eventService).getEventById(eventId);
    }

    @Test
    void updateEvent_whenEventExists_shouldReturnUpdatedEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        ZonedDateTime startAt = ZonedDateTime.parse("2025-10-23T09:30:00+03:00[Europe/Kyiv]");
        ZonedDateTime finishAt = ZonedDateTime.parse("2025-10-23T10:00:00+03:00[Europe/Kyiv]");

        EventDto inputDto = new EventDto(null, "Updated Meeting", "Updated description", startAt, finishAt, "Room B");
        EventDto outputDto = new EventDto(eventId, "Updated Meeting", "Updated description", startAt, finishAt, "Room B");

        when(eventService.updateEvent(eq(eventId), any(EventDto.class))).thenReturn(outputDto);

        mockMvc.perform(put("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Meeting"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.location").value("Room B"));

        verify(eventService).updateEvent(eq(eventId), any(EventDto.class));
    }

    @Test
    void updateEvent_whenEventDoesNotExist_shouldReturnError() throws Exception {
        UUID eventId = UUID.randomUUID();
        ZonedDateTime startAt = ZonedDateTime.parse("2025-10-23T09:30:00+03:00[Europe/Kyiv]");
        ZonedDateTime finishAt = ZonedDateTime.parse("2025-10-23T10:00:00+03:00[Europe/Kyiv]");

        EventDto inputDto = new EventDto(null, "Updated Meeting", "Updated description", startAt, finishAt, "Room B");

        when(eventService.updateEvent(eq(eventId), any(EventDto.class)))
                .thenThrow(new BusinessLogicException("Event not found with id: " + eventId));

        mockMvc.perform(put("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(eventService).updateEvent(eq(eventId), any(EventDto.class));
    }

    @Test
    void getEventPreviewsForMonth_shouldReturnEventPreviews() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        List<EventPreviewDto> previews = List.of(
                new EventPreviewDto(id1, "Event 1",
                        ZonedDateTime.parse("2025-10-10T10:00:00+03:00[Europe/Kyiv]"),
                        ZonedDateTime.parse("2025-10-10T11:00:00+03:00[Europe/Kyiv]"), "Location 1"),
                new EventPreviewDto(id2, "Event 2",
                        ZonedDateTime.parse("2025-10-20T14:00:00+03:00[Europe/Kyiv]"),
                        ZonedDateTime.parse("2025-10-20T15:00:00+03:00[Europe/Kyiv]"), "Location 2")
        );

        when(eventService.getEventPreviewsForMonth(any(ZonedDateTime.class))).thenReturn(previews);

        mockMvc.perform(get("/events/previews/month")
                        .param("date", "2025-10-15T00:00:00+03:00[Europe/Kyiv]"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].title").value("Event 1"))
                .andExpect(jsonPath("$[0].location").value("Location 1"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].title").value("Event 2"))
                .andExpect(jsonPath("$[1].location").value("Location 2"));

        verify(eventService).getEventPreviewsForMonth(any(ZonedDateTime.class));
    }

    @Test
    void getEventPreviewsForMonth_whenNoEventsExist_shouldReturnEmptyList() throws Exception {
        when(eventService.getEventPreviewsForMonth(any(ZonedDateTime.class))).thenReturn(List.of());

        mockMvc.perform(get("/events/previews/month")
                        .param("date", "2025-12-15T00:00:00+03:00[Europe/Kyiv]"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(eventService).getEventPreviewsForMonth(any(ZonedDateTime.class));
    }
}

