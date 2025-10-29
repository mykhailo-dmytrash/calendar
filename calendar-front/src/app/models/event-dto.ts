export interface EventDto {
  id: string;
  title: string;
  description: string;
  startAt: string; // ISO-8601 format with timezone
  finishAt: string; // ISO-8601 format with timezone
  location?: string;
}

