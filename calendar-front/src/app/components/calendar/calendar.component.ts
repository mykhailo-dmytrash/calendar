import {Component, effect, inject, signal} from '@angular/core';
import {CalendarEvent as AngularCalendarEvent, CalendarMonthViewComponent} from 'angular-calendar';
import {SidebarComponent} from './sidebar/sidebar.component';
import {EventService} from '../../services/event.service';
import {CalendarEvent} from '../../models/calendar-event';

@Component({
  selector: 'app-calendar',
  imports: [
    CalendarMonthViewComponent,
    SidebarComponent
  ],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.scss',
})
export class CalendarComponent {

  viewDate = signal(new Date());

  events = signal<AngularCalendarEvent[]>([]);

  selectedDate = signal<Date | null>(null);
  selectedDateEvents = signal<AngularCalendarEvent[]>([]);
  isSidebarOpen = signal(false);

  private eventService = inject(EventService);

  constructor() {
    effect(() => {
      if(this.viewDate()) {
        this.eventService.getEventPreviewsForDate(this.viewDate()!)
          .subscribe(events => {
            this.events.set(events);
          });
      }
    });

    effect(() => {
      const events = this.events();
      const selectedDate = this.selectedDate();
      
      if (selectedDate) {
        const filteredEvents = events.filter(event => {
          const eventDate = event.start;
          return eventDate.getDate() === selectedDate.getDate() &&
                 eventDate.getMonth() === selectedDate.getMonth() &&
                 eventDate.getFullYear() === selectedDate.getFullYear();
        });
        this.selectedDateEvents.set(filteredEvents);
      } else {
        this.selectedDateEvents.set([]);
      }
    });
  }

  onDayClicked(date: Date): void {
    this.selectedDate.set(date);
    this.isSidebarOpen.set(true);
  }

  onSidebarClose(): void {
    this.isSidebarOpen.set(false);
  }

  onRefreshEvents(): void {
    this.eventService.getEventPreviewsForDate(this.viewDate())
      .subscribe(events => {
        this.events.set(events);
      });
  }

  onAddEvent(event: CalendarEvent): void {
    this.eventService.createEvent(event).subscribe({
      next: (createdEvent) => {
        this.events.update(events => [...events, createdEvent]);
      },
      error: (error) => console.error('Error creating event:', error)
    });
  }

  onDeleteEvent(event: CalendarEvent): void {
    if (event.id) {
      this.eventService.deleteEvent(event.id).subscribe({
        next: () => {
          this.events.update(events => events.filter(e => e.id !== event.id));
        },
        error: (error) => console.error('Error deleting event:', error)
      });
    }
  }

  onUpdateEvent(event: CalendarEvent): void {
    if (event.id) {
      this.eventService.updateEvent(event.id, event).subscribe({
        next: (updatedEvent) => {
          this.events.update(events => 
            events.map(e => e.id === updatedEvent.id ? updatedEvent : e)
          );
        },
        error: (error) => console.error('Error updating event:', error)
      });
    }
  }

  previousMonth() {
    this.goToToMonth(-1);
  }

  nextMonth() {
    this.goToToMonth(1);
  }

  goToCurrentMonth() {
    this.viewDate.set(new Date());
  }

  isCurrentMonth(): boolean {
    const currentDate = new Date();
    return this.viewDate().getMonth() === currentDate.getMonth() &&
           this.viewDate().getFullYear() === currentDate.getFullYear();
  }


  private goToToMonth(number: number) {
    const newDate = new Date(this.viewDate());
    newDate.setMonth(this.viewDate().getMonth() + number);
    this.viewDate.set(newDate);
  }

  getMonthYear(): string {
    return this.viewDate().toLocaleDateString(undefined, {
      month: 'long',
      year: 'numeric'
    });
  }
}
