import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event, EventDTO } from '../models/Event';


@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8089/api/events'; 


  constructor(private http: HttpClient) { }

  // Event CRUD Operations
  createEvent(eventData: EventDTO): Observable<Event> {
    return this.http.post<Event>(this.apiUrl, eventData);
  }

  getAllEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl);
  }

  getEventById(id: string): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`);
  }

  updateEvent(id: string, eventData: EventDTO): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${id}`, eventData);
  }

  deleteEvent(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Participant Management
  addParticipant(eventId: string, userId: string): Observable<Event> {
    return this.http.post<Event>(`${this.apiUrl}/${eventId}/participants/${userId}`, {});
  }

  removeParticipant(eventId: string, userId: string): Observable<Event> {
    return this.http.delete<Event>(`${this.apiUrl}/${eventId}/participants/${userId}`);
  }
  registerForEvent(eventId: string, userId: string): Observable<Event> {
    return this.http.post<Event>(
      `${this.apiUrl}/${eventId}/register/${userId}`,
      {}
    );
  }

}