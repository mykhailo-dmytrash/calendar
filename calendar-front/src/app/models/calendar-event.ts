export interface CalendarEvent {
  id?: string;
  title: string;
  start: Date;
  end?: Date;
  description?: string;
  location?: string;
  color?: {
    primary: string;
    secondary: string;
  };
}
