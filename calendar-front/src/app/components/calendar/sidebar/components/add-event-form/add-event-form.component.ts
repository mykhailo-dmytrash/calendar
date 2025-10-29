import { Component, EventEmitter, Input, Output, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { ValidationConstants } from '../../../../../core/constants/validation.constants';
import { CalendarEvent } from '../../../../../models/calendar-event';

@Component({
  selector: 'app-add-event-form',
  imports: [ReactiveFormsModule],
  standalone: true,
  templateUrl: './add-event-form.component.html',
  styleUrl: './add-event-form.component.scss'
})
export class AddEventFormComponent implements OnInit {
  @Input() selectedDate: Date | null = null;
  @Input() eventToEdit: CalendarEvent | null = null;
  @Output() cancel = new EventEmitter<void>();
  @Output() eventCreated = new EventEmitter<CalendarEvent>();
  @Output() eventUpdated = new EventEmitter<CalendarEvent>();

  private fb = inject(FormBuilder);

  eventForm: FormGroup;
  isEditMode = false;

  constructor() {
    this.eventForm = this.fb.group({
      title: ['', [
        Validators.required,
        Validators.pattern(ValidationConstants.ALPHANUMERIC_PATTERN)
      ]],
      description: ['', [
        Validators.required,
        Validators.pattern(ValidationConstants.ALPHANUMERIC_PATTERN)
      ]],
      startTime: ['09:00', [Validators.required]],
      endTime: ['10:00', [Validators.required]],
      location: ['', [
        Validators.pattern(ValidationConstants.ALPHANUMERIC_PATTERN)
      ]]
    }, { validators: this.timeRangeValidator() });
  }

  ngOnInit(): void {
    if (this.eventToEdit) {
      this.isEditMode = true;
      this.populateForm(this.eventToEdit);
    }
  }

  private populateForm(event: CalendarEvent): void {
    const startDate = new Date(event.start);
    const endDate = event.end ? new Date(event.end) : startDate;
    
    const formatTime = (date: Date): string => 
      `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;

    this.eventForm.patchValue({
      title: event.title,
      description: event.description || '',
      startTime: formatTime(startDate),
      endTime: formatTime(endDate),
      location: event.location || ''
    });
  }

  onSave(): void {
    if (this.eventForm.valid) {
      if (this.isEditMode && this.eventToEdit?.id) {
        const eventDate = this.eventToEdit.start;
        const startDate = new Date(eventDate);
        const startTime = this.eventForm.value.startTime.split(':').map(Number);
        startDate.setHours(startTime[0], startTime[1], 0, 0);

        const endDate = new Date(eventDate);
        const endTime = this.eventForm.value.endTime.split(':').map(Number);
        endDate.setHours(endTime[0], endTime[1], 0, 0);

        const updatedEvent = {
          ...this.eventToEdit,
          title: this.eventForm.value.title,
          description: this.eventForm.value.description,
          location: this.eventForm.value.location,
          start: startDate,
          end: endDate
        };

        this.eventUpdated.emit(updatedEvent);
        this.resetForm();
      } else if (this.selectedDate) {
        const startDate = new Date(this.selectedDate);
        const startTime = this.eventForm.value.startTime.split(':').map(Number);
        startDate.setHours(startTime[0], startTime[1], 0, 0);

        const endDate = new Date(this.selectedDate);
        const endTime = this.eventForm.value.endTime.split(':').map(Number);
        endDate.setHours(endTime[0], endTime[1], 0, 0);

        const newEvent: CalendarEvent = {
          title: this.eventForm.value.title,
          description: this.eventForm.value.description,
          start: startDate,
          end: endDate,
          location: this.eventForm.value.location || undefined
        };

        this.eventCreated.emit(newEvent);
        this.resetForm();
      }
    } else {
      this.markFormGroupTouched(this.eventForm);
    }
  }

  onCancel(): void {
    this.cancel.emit();
    this.resetForm();
  }

  private timeRangeValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const startTime = control.get('startTime')?.value;
      const endTime = control.get('endTime')?.value;

      if (!startTime || !endTime) {
        return null;
      }

      const [startHour, startMinute] = startTime.split(':').map(Number);
      const [endHour, endMinute] = endTime.split(':').map(Number);

      const startMinutes = startHour * 60 + startMinute;
      const endMinutes = endHour * 60 + endMinute;

      return startMinutes >= endMinutes ? { timeRange: true } : null;
    };
  }

  private resetForm(): void {
    this.eventForm.reset({
      title: '',
      description: '',
      startTime: '09:00',
      endTime: '10:00',
      location: ''
    });
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string {
    const control = this.eventForm.get(fieldName);
    if (control && control.touched && control.errors) {
      if (control.errors['required']) {
        return ValidationConstants.MESSAGES.FIELD_REQUIRED(
          fieldName.charAt(0).toUpperCase() + fieldName.slice(1)
        );
      }
      if (control.errors['pattern']) {
        return ValidationConstants.MESSAGES.INVALID_PATTERN;
      }
    }
    return '';
  }

  getTimeRangeError(): string {
    if (this.eventForm.errors?.['timeRange'] && 
        (this.eventForm.get('startTime')?.touched || this.eventForm.get('endTime')?.touched)) {
      return ValidationConstants.MESSAGES.START_TIME_BEFORE_END_TIME;
    }
    return '';
  }

  hasTimeRangeError(): boolean {
    return !!(this.eventForm.errors?.['timeRange'] && 
              (this.eventForm.get('startTime')?.touched || this.eventForm.get('endTime')?.touched));
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.eventForm.get(fieldName);
    return !!(control?.touched && control.invalid);
  }
}
