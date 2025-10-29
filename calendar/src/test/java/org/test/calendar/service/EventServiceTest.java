package org.test.calendar.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test.calendar.exception.BusinessLogicException;
import org.test.calendar.mapper.EventMapper;
import org.test.calendar.model.dto.EventDto;
import org.test.calendar.model.dto.EventPreviewDto;
import org.test.calendar.model.entity.EventEntity;
import org.test.calendar.repository.EventRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    @Test
    void createEvent_shouldSaveAndReturnEventDto() {
        ZonedDateTime startAt = ZonedDateTime.parse("2025-10-23T09:30:00+03:00[Europe/Kyiv]");
        ZonedDateTime finishAt = ZonedDateTime.parse("2025-10-23T10:00:00+03:00[Europe/Kyiv]");
        UUID generatedId = UUID.randomUUID();

        EventDto inputDto = new EventDto(null, "Team Meeting", "Daily standup", startAt, finishAt, "Room A");
        EventEntity entity = new EventEntity(null, "Team Meeting", "Daily standup", 
                startAt.toInstant(), finishAt.toInstant(), "Room A");
        EventDto outputDto = new EventDto(generatedId, "Team Meeting", "Daily standup", startAt, finishAt, "Room A");

        when(eventMapper.toEventEntity(inputDto)).thenReturn(entity);
        when(eventRepository.save(any(EventEntity.class))).thenReturn(
                new EventEntity(generatedId, "Team Meeting", "Daily standup", 
                        startAt.toInstant(), finishAt.toInstant(), "Room A")
        );
        when(eventMapper.toEventDto(any(EventEntity.class))).thenReturn(outputDto);

        EventDto result = eventService.createEvent(inputDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(generatedId);
        assertThat(result.title()).isEqualTo("Team Meeting");
        assertThat(result.description()).isEqualTo("Daily standup");
        
        verify(eventMapper).toEventEntity(inputDto);
        verify(eventRepository).save(any(EventEntity.class));
        verify(eventMapper).toEventDto(any(EventEntity.class));
    }

    @Test
    void deleteEvent_shouldCallRepositoryDelete() {
        UUID eventId = UUID.randomUUID();

        eventService.deleteEvent(eventId);

        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void getEventById_whenEventExists_shouldReturnEventDto() {
        UUID eventId = UUID.randomUUID();
        ZonedDateTime startAt = ZonedDateTime.parse("2025-10-23T09:30:00+03:00[Europe/Kyiv]");
        ZonedDateTime finishAt = ZonedDateTime.parse("2025-10-23T10:00:00+03:00[Europe/Kyiv]");

        EventEntity entity = new EventEntity(eventId, "Team Meeting", "Daily standup", 
                startAt.toInstant(), finishAt.toInstant(), "Room A");
        EventDto dto = new EventDto(eventId, "Team Meeting", "Daily standup", startAt, finishAt, "Room A");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(entity));
        when(eventMapper.toEventDto(entity)).thenReturn(dto);

        EventDto result = eventService.getEventById(eventId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(eventId);
        assertThat(result.title()).isEqualTo("Team Meeting");
        
        verify(eventRepository).findById(eventId);
        verify(eventMapper).toEventDto(entity);
    }

    @Test
    void getEventById_whenEventDoesNotExist_shouldThrowBusinessLogicException() {
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(eventId))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Event not found with id: " + eventId);

        verify(eventRepository).findById(eventId);
        verify(eventMapper, never()).toEventDto(any());
    }

    @Test
    void updateEvent_whenEventExists_shouldUpdateAndReturnEventDto() {
        UUID eventId = UUID.randomUUID();
        ZonedDateTime startAt = ZonedDateTime.parse("2025-10-23T09:30:00+03:00[Europe/Kyiv]");
        ZonedDateTime finishAt = ZonedDateTime.parse("2025-10-23T10:00:00+03:00[Europe/Kyiv]");

        EventDto updateDto = new EventDto(null, "Updated Title", "Updated Description", startAt, finishAt, "Room B");
        EventEntity existingEntity = new EventEntity(eventId, "Old Title", "Old Description", 
                startAt.toInstant(), finishAt.toInstant(), "Room A");
        EventEntity updatedEntity = new EventEntity(eventId, "Updated Title", "Updated Description", 
                startAt.toInstant(), finishAt.toInstant(), "Room B");
        EventDto resultDto = new EventDto(eventId, "Updated Title", "Updated Description", startAt, finishAt, "Room B");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEntity));
        when(eventRepository.save(any(EventEntity.class))).thenReturn(updatedEntity);
        when(eventMapper.toEventDto(updatedEntity)).thenReturn(resultDto);

        EventDto result = eventService.updateEvent(eventId, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(eventId);
        assertThat(result.title()).isEqualTo("Updated Title");
        assertThat(result.description()).isEqualTo("Updated Description");
        assertThat(result.location()).isEqualTo("Room B");
        
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(any(EventEntity.class));
        verify(eventMapper).toEventDto(updatedEntity);
    }

    @Test
    void updateEvent_whenEventDoesNotExist_shouldThrowBusinessLogicException() {
        UUID eventId = UUID.randomUUID();
        ZonedDateTime startAt = ZonedDateTime.parse("2025-10-23T09:30:00+03:00[Europe/Kyiv]");
        ZonedDateTime finishAt = ZonedDateTime.parse("2025-10-23T10:00:00+03:00[Europe/Kyiv]");

        EventDto updateDto = new EventDto(null, "Updated Title", "Updated Description", startAt, finishAt, "Room B");

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(eventId, updateDto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Event not found with id: " + eventId);

        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).save(any());
        verify(eventMapper, never()).toEventDto(any());
    }

    @Test
    void getEventPreviewsForMonth_shouldReturnEventPreviewsForSpecifiedMonth() {
        ZonedDateTime date = ZonedDateTime.of(2025, 10, 15, 0, 0, 0, 0, ZoneId.of("UTC"));
        
        Instant startOfMonth = Instant.parse("2025-10-01T00:00:00Z");
        Instant startOfNextMonth = Instant.parse("2025-11-01T00:00:00Z");

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        
        List<EventEntity> entities = List.of(
                new EventEntity(id1, "Event 1", "Description 1",
                        Instant.parse("2025-10-10T10:00:00Z"),
                        Instant.parse("2025-10-10T11:00:00Z"), "Location 1"),
                new EventEntity(id2, "Event 2", "Description 2",
                        Instant.parse("2025-10-20T14:00:00Z"),
                        Instant.parse("2025-10-20T15:00:00Z"), "Location 2")
        );

        EventPreviewDto preview1 = new EventPreviewDto(id1, "Event 1",
                ZonedDateTime.parse("2025-10-10T10:00:00Z"),
                ZonedDateTime.parse("2025-10-10T11:00:00Z"), "Location 1");
        EventPreviewDto preview2 = new EventPreviewDto(id2, "Event 2",
                ZonedDateTime.parse("2025-10-20T14:00:00Z"),
                ZonedDateTime.parse("2025-10-20T15:00:00Z"), "Location 2");

        when(eventRepository.findByStartAtBetween(startOfMonth, startOfNextMonth)).thenReturn(entities);
        when(eventMapper.toEventPreviewDto(entities.get(0))).thenReturn(preview1);
        when(eventMapper.toEventPreviewDto(entities.get(1))).thenReturn(preview2);

        List<EventPreviewDto> result = eventService.getEventPreviewsForMonth(date);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(preview1, preview2);
        
        verify(eventRepository).findByStartAtBetween(startOfMonth, startOfNextMonth);
        verify(eventMapper, times(2)).toEventPreviewDto(any(EventEntity.class));
    }

    @Test
    void getEventPreviewsForMonth_whenNoEventsExist_shouldReturnEmptyList() {
        ZonedDateTime date = ZonedDateTime.of(2025, 12, 15, 0, 0, 0, 0, ZoneId.of("UTC"));
        
        Instant startOfMonth = Instant.parse("2025-12-01T00:00:00Z");
        Instant startOfNextMonth = Instant.parse("2026-01-01T00:00:00Z");

        when(eventRepository.findByStartAtBetween(startOfMonth, startOfNextMonth)).thenReturn(List.of());

        List<EventPreviewDto> result = eventService.getEventPreviewsForMonth(date);

        assertThat(result).isEmpty();
        
        verify(eventRepository).findByStartAtBetween(startOfMonth, startOfNextMonth);
        verify(eventMapper, never()).toEventPreviewDto(any());
    }

    @Test
    void getEventPreviewsForMonth_withDifferentTimezone_shouldCalculateCorrectRange() {
        ZonedDateTime date = ZonedDateTime.of(2025, 10, 15, 0, 0, 0, 0, ZoneId.of("Europe/Kyiv"));
        
        Instant startOfMonth = ZonedDateTime.of(2025, 10, 1, 0, 0, 0, 0, ZoneId.of("Europe/Kyiv")).toInstant();
        Instant startOfNextMonth = ZonedDateTime.of(2025, 11, 1, 0, 0, 0, 0, ZoneId.of("Europe/Kyiv")).toInstant();

        when(eventRepository.findByStartAtBetween(startOfMonth, startOfNextMonth)).thenReturn(List.of());

        List<EventPreviewDto> result = eventService.getEventPreviewsForMonth(date);

        assertThat(result).isEmpty();
        
        verify(eventRepository).findByStartAtBetween(startOfMonth, startOfNextMonth);
    }
}

