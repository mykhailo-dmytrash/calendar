import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { AppConfig } from '../config/app.config';
import { EventDto } from '../models/event-dto';
import { CalendarEvent } from '../models/calendar-event';
import { EventMapperService } from './event-mapper.service';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private http = inject(HttpClient);
  private mapper = inject(EventMapperService);
  private apiUrl = AppConfig.apiUrl;

  getEventPreviewsForDate(date: Date): Observable<CalendarEvent[]> {
    return this.http.get<EventDto[]>(`${this.apiUrl}/events/previews/month`, {
      params: {
        date: date.toISOString()
      }
    }).pipe(map(dtos => this.mapper.toCalendarEvents(dtos)));
  }

  getEvents(start: Date, end: Date): Observable<CalendarEvent[]> {
    return this.http.get<EventDto[]>(`${this.apiUrl}/events`, {
      params: {
        start: start.toISOString(),
        end: end.toISOString()
      }
    }).pipe(map(dtos => this.mapper.toCalendarEvents(dtos)));
  }

  getEvent(id: string): Observable<CalendarEvent> {
    return this.http.get<EventDto>(`${this.apiUrl}/events/${id}`)
      .pipe(map(dto => this.mapper.toCalendarEvent(dto)));
  }

  createEvent(event: CalendarEvent): Observable<CalendarEvent> {
    const dto = this.mapper.toEventDto(event);
    return this.http.post<EventDto>(`${this.apiUrl}/events`, dto)
      .pipe(map(dto => this.mapper.toCalendarEvent(dto)));
  }

  updateEvent(id: string, event: CalendarEvent): Observable<CalendarEvent> {
    const dto = this.mapper.toEventDto(event);
    return this.http.put<EventDto>(`${this.apiUrl}/events/${id}`, dto)
      .pipe(map(dto => this.mapper.toCalendarEvent(dto)));
  }

  deleteEvent(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/events/${id}`);
  }
}


