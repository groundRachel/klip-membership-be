package com.klipwallet.membership.dto.datetime;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

@Component
public class DateTimeAssembler {
    private static final String DEFAULT_OFFSET_ID = "+09:00";
    private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.of(DEFAULT_OFFSET_ID);

    public OffsetDateTime toOffsetDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atOffset(DEFAULT_OFFSET);
    }

    public LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.withOffsetSameInstant(DEFAULT_OFFSET).toLocalDateTime();
    }
}
