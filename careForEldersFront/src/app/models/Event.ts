// models/Event.ts
export interface Event {
  target: HTMLInputElement;
  id?: string;
  title: string;
  date: Date | string;
  location: string;
  description: string;
  participantIds: string[];
}
export interface EventDTO {
  title: string;
  date: Date;
  location: string;
  description: string;
  participantIds: string[];
}
export interface User {
  id: string;
  name: string;
 role: string;
}