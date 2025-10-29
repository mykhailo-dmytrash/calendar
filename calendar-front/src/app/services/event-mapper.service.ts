import { Injectable } from '@angular/core';
import { CalendarEvent } from '../models/calendar-event';
import { EventDto } from '../models/event-dto';

@Injectable({
  providedIn: 'root'
})
export class EventMapperService {

  toCalendarEvent(dto: EventDto): CalendarEvent {
    return {
      id: dto.id,
      title: dto.title,
      description: dto.description,
      location: dto.location,
      start: new Date(dto.startAt),
      end: new Date(dto.finishAt)
    };
  }

  toCalendarEvents(dtos: EventDto[]): CalendarEvent[] {
    return dtos.map(dto => this.toCalendarEvent(dto));
  }

  toEventDto(event: CalendarEvent): Partial<EventDto> {
    const dto: Partial<EventDto> = {
      title: event.title,
      description: event.description || '',
      startAt: event.start.toISOString(),
      finishAt: event.end?.toISOString() || event.start.toISOString()
    };
    
    if (event.location) {
      dto.location = event.location;
    }
    
    return dto;
  }
}

