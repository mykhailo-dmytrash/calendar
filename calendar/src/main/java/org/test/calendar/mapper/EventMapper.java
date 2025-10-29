package org.test.calendar.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.test.calendar.model.dto.EventDto;
import org.test.calendar.model.dto.EventPreviewDto;
import org.test.calendar.model.entity.EventEntity;
import org.test.calendar.util.DateUtil;

import java.time.Instant;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "finishAt", source = "finishAt")
    @Mapping(target = "location", source = "location")
    EventEntity toEventEntity(EventDto eventDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "finishAt", source = "finishAt")
    @Mapping(target = "location", source = "location")
    EventDto toEventDto(EventEntity eventEntity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "startAt", source = "startAt")
    @Mapping(target = "finishAt", source = "finishAt")
    @Mapping(target = "location", source = "location")
    EventPreviewDto toEventPreviewDto(EventEntity eventEntity);

    default Instant map(ZonedDateTime value) {
        return DateUtil.toInstant(value);
    }

    default ZonedDateTime map(Instant value) {
        return DateUtil.toZonedDateTime(value);
    }
}
