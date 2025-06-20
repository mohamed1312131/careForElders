import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';



export interface Reservation {
  id: string;
  date: string;
  heure: string;
  endTime: string;
  statut: string;
  userId: string;
  doctorId: string;
  reservationType: string;
  meetingLink: string;
  location: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8083/api/reservations';

  constructor(private http: HttpClient) {}

  getAllReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.apiUrl);
  }
}
