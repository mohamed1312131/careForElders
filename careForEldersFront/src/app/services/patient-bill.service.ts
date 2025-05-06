// src/app/core/services/patient-bill.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { PatientBill } from '../models/patient-bill.model';

@Injectable({
  providedIn: 'root'
})
export class PatientBillService {
  private apiUrl = 'api/patient-bills'; // Replace with your actual API endpoint

  constructor(private http: HttpClient) {}

  getPatientBills(): Observable<PatientBill[]> {
    return this.http.get<PatientBill[]>(this.apiUrl).pipe(
      catchError(this.handleError<PatientBill[]>('getPatientBills', []))
    );
  }

  getPatientBill(id: number): Observable<PatientBill> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<PatientBill>(url).pipe(
      catchError(this.handleError<PatientBill>(`getPatientBill id=${id}`))
    );
  }

  createPatientBill(bill: PatientBill): Observable<PatientBill> {
    return this.http.post<PatientBill>(this.apiUrl, bill).pipe(
      catchError(this.handleError<PatientBill>('createPatientBill'))
    );
  }

  updatePatientBill(bill: PatientBill): Observable<PatientBill> {
    return this.http.put<PatientBill>(`${this.apiUrl}/${bill.id}`, bill).pipe(
      catchError(this.handleError<PatientBill>('updatePatientBill'))
    );
  }

  deletePatientBill(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<any>(url).pipe(
      catchError(this.handleError<any>('deletePatientBill'))
    );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      // Let the app keep running by returning an empty result
      return of(result as T);
    };
  }
}