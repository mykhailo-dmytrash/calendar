package org.test.calendar.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

@UtilityClass
public class DateUtil {

    public Instant toInstant(ZonedDateTime value) {
        return Optional.ofNullable(value)
                .map(ZonedDateTime::toInstant)
                .orElse(null);
    }

    public ZonedDateTime toZonedDateTime(Instant value) {
        return Optional.ofNullable(value)
                .map(v -> v.atZone(java.time.ZoneOffset.UTC))
                .orElse(null);
    }
}
