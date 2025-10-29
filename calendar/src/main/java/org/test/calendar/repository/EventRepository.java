package org.test.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.calendar.model.entity.EventEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {

    List<EventEntity> findByStartAtBetween(Instant startOfMonth, Instant startOfNextMonth);
}
