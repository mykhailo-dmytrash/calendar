import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./components/calendar/calendar.component').then(m => m.CalendarComponent) }
];
