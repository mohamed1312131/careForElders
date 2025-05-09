import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DisponibiliteService {
  private apiUrl = 'http://localhost:8083/api/disponibilites';

  constructor(private http: HttpClient) {}

  getByDoctor(doctorId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/doctor/${doctorId}`);
  }
}
