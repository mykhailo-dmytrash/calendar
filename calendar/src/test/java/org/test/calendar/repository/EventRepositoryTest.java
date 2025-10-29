package org.test.calendar.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.test.calendar.model.entity.EventEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        List<EventEntity> events = List.of(
                new EventEntity(
                        null,
                        "Morning Meeting",
                        "Daily standup meeting with the team",
                        Instant.parse("2025-10-15T08:00:00Z"),
                        Instant.parse("2025-10-15T09:00:00Z"),
                        "Conference Room A"
                ),
                new EventEntity(
                        null,
                        "Lunch Break",
                        "Team lunch",
                        Instant.parse("2025-10-15T12:00:00Z"),
                        Instant.parse("2025-10-15T13:00:00Z"),
                        "Cafeteria"
                ),
                new EventEntity(
                        null,
                        "Project Review",
                        "Monthly project review meeting",
                        Instant.parse("2025-10-20T14:00:00Z"),
                        Instant.parse("2025-10-20T16:00:00Z"),
                        "Conference Room B"
                ),
                new EventEntity(
                        null,
                        "Training Session",
                        "Technical training on new framework",
                        Instant.parse("2025-11-05T10:00:00Z"),
                        Instant.parse("2025-11-05T12:00:00Z"),
                        "Training Room"
                ),
                new EventEntity(
                        null,
                        "Team Building",
                        "Quarterly team building activity",
                        Instant.parse("2025-09-28T15:00:00Z"),
                        Instant.parse("2025-09-28T18:00:00Z"),
                        "Outdoor Park"
                )
        );

        eventRepository.saveAll(events);
    }

    @Test
    void findByStartAtBetween_shouldReturnEventsInOctoberRange() {
        Instant startOfOctober = Instant.parse("2025-10-01T00:00:00Z");
        Instant endOfOctober = Instant.parse("2025-10-31T23:59:59Z");

        List<EventEntity> events = eventRepository.findByStartAtBetween(startOfOctober, endOfOctober);

        assertThat(events).hasSize(3);
        assertThat(events).extracting(EventEntity::getTitle)
                .containsExactlyInAnyOrder("Morning Meeting", "Lunch Break", "Project Review");
    }

    @Test
    void findByStartAtBetween_shouldReturnEventsInNovemberRange() {
        Instant startOfNovember = Instant.parse("2025-11-01T00:00:00Z");
        Instant endOfNovember = Instant.parse("2025-11-30T23:59:59Z");

        List<EventEntity> events = eventRepository.findByStartAtBetween(startOfNovember, endOfNovember);

        assertThat(events).hasSize(1);
        
        EventEntity event = events.getFirst();
        assertThat(event.getTitle()).isEqualTo("Training Session");
        assertThat(event.getDescription()).isEqualTo("Technical training on new framework");
        assertThat(event.getLocation()).isEqualTo("Training Room");
    }

    @Test
    void findByStartAtBetween_shouldReturnEmptyListWhenNoEventsInRange() {
        Instant startOfDecember = Instant.parse("2025-12-01T00:00:00Z");
        Instant endOfDecember = Instant.parse("2025-12-31T23:59:59Z");

        List<EventEntity> events = eventRepository.findByStartAtBetween(startOfDecember, endOfDecember);

        assertThat(events).isEmpty();
    }

    @Test
    void findByStartAtBetween_shouldReturnAllEventsWhenRangeCoversAll() {
        Instant startDate = Instant.parse("2025-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2025-12-31T23:59:59Z");

        List<EventEntity> events = eventRepository.findByStartAtBetween(startDate, endDate);

        assertThat(events).hasSize(5);
        assertThat(events).extracting(EventEntity::getTitle)
                .containsExactlyInAnyOrder(
                        "Morning Meeting",
                        "Lunch Break",
                        "Project Review",
                        "Training Session",
                        "Team Building"
                );
    }

    @Test
    void findByStartAtBetween_shouldReturnEventsOnExactBoundary() {
        Instant exactStart = Instant.parse("2025-10-15T08:00:00Z");
        Instant exactEnd = Instant.parse("2025-10-15T12:00:00Z");

        List<EventEntity> events = eventRepository.findByStartAtBetween(exactStart, exactEnd);

        assertThat(events).hasSize(2);
        assertThat(events).extracting(EventEntity::getTitle)
                .containsExactlyInAnyOrder("Morning Meeting", "Lunch Break");
    }
}

