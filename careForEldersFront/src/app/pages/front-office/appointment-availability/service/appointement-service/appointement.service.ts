import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppointementService {
 // private apiUrl = 'http://localhost:8081/users'; // Replace with your actual backend URL

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<any> {
  return this.http.get('http://localhost:8083/reservations/getAllUsers');
}
getDoctorById(doctorId: string): Observable<any> {
  return this.http.get<any>(`http://localhost:8083/reservations/getUser/${doctorId}`);
}
getAllDoctors(): Observable<any>{
  return this.http.get('http://localhost:8083/reservations/getAllDoctors');
}
}