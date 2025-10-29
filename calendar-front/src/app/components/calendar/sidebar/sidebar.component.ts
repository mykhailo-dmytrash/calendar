import {Component, EventEmitter, inject, Input, Output, signal} from '@angular/core';
import {CalendarEvent as AngularCalendarEvent} from 'angular-calendar';
import {EventListComponent} from './components/event-list/event-list.component';
import {AddEventFormComponent} from './components/add-event-form/add-event-form.component';
import {EventDetailsComponent} from './components/event-details/event-details.component';
import {EventService} from '../../../services/event.service';
import {DateUtils} from '../../../core/utils/date.utils';
import {CalendarEvent} from '../../../models/calendar-event';

@Component({
  selector: 'app-calendar-sidebar',
  imports: [EventListComponent, AddEventFormComponent, EventDetailsComponent],
  standalone: true,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  private eventService = inject(EventService);
  @Input()
  isOpen = false;

  @Input()
  selectedDate: Date | null = null;

  @Input()
  events: AngularCalendarEvent[] = [];

  @Output()
  close = new EventEmitter<void>();

  @Output()
  addEvent = new EventEmitter<CalendarEvent>();

  @Output()
  refreshEvents = new EventEmitter<void>();

  @Output()
  deleteEvent = new EventEmitter<CalendarEvent>();

  @Output()
  updateEvent = new EventEmitter<CalendarEvent>();

  showAddEvent = signal(false);
  showEventDetails = signal(false);
  showUpdateForm = signal(false);
  selectedEvent: CalendarEvent | null = null;

  closeSidebar(): void {
    this.close.emit();
    this.resetView();
  }

  private resetView(): void {
    this.showAddEvent.set(false);
    this.showEventDetails.set(false);
    this.showUpdateForm.set(false);
    this.selectedEvent = null;
  }

  onBackToDetails(): void {
    this.showUpdateForm.set(false);
    this.showEventDetails.set(true);
  }

  onAddEventClick(): void {
    this.showAddEvent.set(true);
  }

  onEventCreated(event: CalendarEvent): void {
    this.addEvent.emit(event);
    this.showAddEvent.set(false);
  }

  onEventSelected(event: CalendarEvent): void {
    if (event.id) {
      this.eventService.getEvent(event.id).subscribe({
        next: (fullEvent) => {
          this.selectedEvent = fullEvent;
          this.showEventDetails.set(true);
        },
        error: (error) => {
          console.error('Error fetching event details:', error);
          this.selectedEvent = event;
          this.showEventDetails.set(true);
        }
      });
    } else {
      this.selectedEvent = event;
      this.showEventDetails.set(true);
    }
  }

  onBackToEvents(): void {
    this.showAddEvent.set(false);
    this.showEventDetails.set(false);
    this.showUpdateForm.set(false);
  }

  onDeleteFromDetails(): void {
    if (this.selectedEvent) {
      this.deleteEvent.emit(this.selectedEvent);
    }
    this.showEventDetails.set(false);
  }

  onUpdateFromDetails(): void {
    this.showEventDetails.set(false);
    this.showUpdateForm.set(true);
  }

  onUpdateComplete(event: CalendarEvent): void {
    this.updateEvent.emit(event);
    this.showUpdateForm.set(false);
  }

  getSelectedDateString(): string {
    return DateUtils.formatDateForDisplay(this.selectedDate);
  }
}


