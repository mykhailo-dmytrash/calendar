import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DateUtils } from '../../../../../core/utils/date.utils';
import { CalendarEvent } from '../../../../../models/calendar-event';

@Component({
  selector: 'app-event-details',
  imports: [],
  standalone: true,
  templateUrl: './event-details.component.html',
  styleUrl: './event-details.component.scss'
})
export class EventDetailsComponent {
  dateUtils = DateUtils;
  
  @Input() event: CalendarEvent | null = null;
  @Output() back = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();
  @Output() update = new EventEmitter<void>();

  onBack(): void {
    this.back.emit();
  }

  onDelete(): void {
    this.delete.emit();
  }

  onUpdate(): void {
    this.update.emit();
  }
}

