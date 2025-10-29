package org.test.calendar.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.calendar.exception.BusinessLogicException;
import org.test.calendar.mapper.EventMapper;
import org.test.calendar.model.dto.EventDto;
import org.test.calendar.model.dto.EventPreviewDto;
import org.test.calendar.model.entity.EventEntity;
import org.test.calendar.repository.EventRepository;
import org.test.calendar.util.DateUtil;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventDto createEvent(@NonNull EventDto eventDto) {
        EventEntity eventEntity = eventMapper.toEventEntity(eventDto);
        eventRepository.save(eventEntity);
        return  eventMapper.toEventDto(eventEntity);
    }

    @Transactional
    public void deleteEvent(@NonNull UUID id) {
        eventRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public EventDto getEventById(@NonNull UUID id) {
        EventEntity eventEntity = getEventEntityById(id);
        return eventMapper.toEventDto(eventEntity);
    }

    @Transactional
    public  EventDto updateEvent(@NonNull UUID id, @NonNull EventDto eventDto) {
        EventEntity eventEntity = getEventEntityById(id);

        eventEntity = eventEntity.withTitle(eventDto.title())
                     .withDescription(eventDto.description())
                     .withStartAt(DateUtil.toInstant(eventDto.startAt()))
                     .withFinishAt(DateUtil.toInstant(eventDto.finishAt()))
                     .withLocation(eventDto.location());

        return eventMapper.toEventDto(eventRepository.save(eventEntity));
    }

    private EventEntity getEventEntityById(@NonNull UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException("Event not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<EventPreviewDto> getEventPreviewsForMonth(ZonedDateTime date) {
        log.debug("Getting event previews for date: {}", date);

        int year = date.getYear();
        int month = date.getMonthValue();
        ZoneId zoneId = date.getZone();

        YearMonth yearMonth = YearMonth.of(year, month);
        Instant startOfMonth = yearMonth.atDay(1)
                .atStartOfDay(zoneId)
                .toInstant();

        Instant startOfNextMonth = yearMonth.plusMonths(1)
                .atDay(1)
                .atStartOfDay(zoneId)
                .toInstant();
        
        List<EventEntity> events = eventRepository.findByStartAtBetween(startOfMonth, startOfNextMonth);
        
        return events.stream()
                .map(eventMapper::toEventPreviewDto)
                .toList();
    }
}
