import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DateUtils } from '../../../../../core/utils/date.utils';
import { CalendarEvent } from '../../../../../models/calendar-event';

@Component({
  selector: 'app-event-list',
  imports: [],
  standalone: true,
  templateUrl: './event-list.component.html',
  styleUrl: './event-list.component.scss'
})
export class EventListComponent {
  dateUtils = DateUtils;
  
  @Input() events: any[] = [];
  @Output() addEvent = new EventEmitter<void>();
  @Output() eventSelected = new EventEmitter<CalendarEvent>();

  onAddEventClick(): void {
    this.addEvent.emit();
  }

  onEventClick(event: any): void {
    this.eventSelected.emit(event as CalendarEvent);
  }
}
