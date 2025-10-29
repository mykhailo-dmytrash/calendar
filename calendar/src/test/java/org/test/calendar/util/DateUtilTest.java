package org.test.calendar.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilTest {

    @Test
    void toInstant_whenZonedDateTimeIsValid_shouldConvertToInstant() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(
                2025, 10, 29, 14, 30, 0, 0,
                ZoneOffset.UTC
        );
        Instant expectedInstant = zonedDateTime.toInstant();

        Instant result = DateUtil.toInstant(zonedDateTime);

        assertThat(result).isEqualTo(expectedInstant);
    }

    @Test
    void toInstant_whenZonedDateTimeIsInDifferentTimezone_shouldConvertCorrectly() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(
                2025, 10, 29, 14, 30, 0, 0,
                ZoneId.of("America/New_York")
        );
        Instant expectedInstant = zonedDateTime.toInstant();

        Instant result = DateUtil.toInstant(zonedDateTime);

        assertThat(result).isEqualTo(expectedInstant);
    }

    @Test
    void toInstant_whenZonedDateTimeIsNull_shouldReturnNull() {
        Instant result = DateUtil.toInstant(null);

        assertThat(result).isNull();
    }

    @Test
    void toZonedDateTime_whenInstantIsValid_shouldConvertToZonedDateTimeInUTC() {
        Instant instant = Instant.parse("2025-10-29T14:30:00Z");
        ZonedDateTime expectedZonedDateTime = instant.atZone(ZoneOffset.UTC);

        ZonedDateTime result = DateUtil.toZonedDateTime(instant);

        assertThat(result).isEqualTo(expectedZonedDateTime);
        assertThat(result.getZone()).isEqualTo(ZoneOffset.UTC);
    }

    @Test
    void toZonedDateTime_whenInstantIsAtEpoch_shouldConvertCorrectly() {
        Instant instant = Instant.EPOCH;
        ZonedDateTime expectedZonedDateTime = ZonedDateTime.of(
                1970, 1, 1, 0, 0, 0, 0,
                ZoneOffset.UTC
        );

        ZonedDateTime result = DateUtil.toZonedDateTime(instant);

        assertThat(result).isEqualTo(expectedZonedDateTime);
    }

    @Test
    void toZonedDateTime_whenInstantIsNull_shouldReturnNull() {
        ZonedDateTime result = DateUtil.toZonedDateTime(null);

        assertThat(result).isNull();
    }

    @Test
    void roundTrip_shouldPreserveInstantValue() {
        Instant originalInstant = Instant.parse("2025-10-29T14:30:00Z");

        ZonedDateTime zonedDateTime = DateUtil.toZonedDateTime(originalInstant);
        Instant resultInstant = DateUtil.toInstant(zonedDateTime);

        assertThat(resultInstant).isEqualTo(originalInstant);
    }

    @Test
    void roundTrip_withDifferentTimezone_shouldPreserveInstantValue() {
        ZonedDateTime originalZonedDateTime = ZonedDateTime.of(
                2025, 10, 29, 14, 30, 0, 0,
                ZoneId.of("Europe/Paris")
        );
        Instant expectedInstant = originalZonedDateTime.toInstant();

        Instant instant = DateUtil.toInstant(originalZonedDateTime);
        ZonedDateTime zonedDateTime = DateUtil.toZonedDateTime(instant);
        Instant resultInstant = DateUtil.toInstant(zonedDateTime);

        assertThat(instant).isEqualTo(expectedInstant);
        assertThat(resultInstant).isEqualTo(expectedInstant);
        assertThat(zonedDateTime.getZone()).isEqualTo(ZoneOffset.UTC);
    }
}

