import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {map} from "rxjs/operators";
export interface MedicalDocument {
  _id: string;
  userId: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  data: string; // Base64 encoded content
  uploadDate: Date | string;
  // No 'url' property exists in your backend response
}
export interface MedicalRecord {
  _id: string;
  userId: string;
  bloodType?: string;
  allergies?: string[] | string;
  currentMedications?: { name: string; dosage: string }[] | string;
  chronicConditions?: string[] | string;
  primaryCarePhysician?: string;
  lastPhysicalExam?: Date | null;
  // Add other fields that exist in your backend MedicalRecord model
}

@Injectable({ providedIn: 'root' })
export class MedicalRecordService {
    private baseUrl = 'http://localhost:8082/api'; // Proxy to Spring Boot

  constructor(private http: HttpClient) {}

  getMedicalNotes(userId: string | null): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/notes/user/${userId}`);
  }

  getAppointments(userId: string | null): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/appointments/patient/${userId}`);
  }

  addMedicalNote(note: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/notes`, note);
  }

  deleteMedicalNote(noteId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/notes/${noteId}`);
  }

  getDocumentContent(documentId: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/documents/${documentId}/content`, {
      responseType: 'blob'
    });
  }

  getDocumentsForUser(userId: string): Observable<MedicalDocument[]> {
    return this.http.get<MedicalDocument[]>(`${this.baseUrl}/documents/user/${userId}`);
  }

  uploadDocument(file: File, userId: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId);

    return this.http.post(`${this.baseUrl}/documents/upload`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  deleteDocument(docId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/documents/${docId}`);
  }
  getMedicalRecord(userId: string) {
    return this.http.get<any>(`${this.baseUrl}/medical-records/user/${userId}`);
  }
  updateMedicalRecord(id: string, record: any) {
    return this.http.put(`${this.baseUrl}/medical-records/${id}`, record);
  }
  createMedicalRecord(data: any) {
    return this.http.post(`${this.baseUrl}/medical-records`, data);
  }

// Add to your medical-record.service.ts
  getAllMedicalRecords(): Observable<MedicalRecord[]> {
    return this.http.get<MedicalRecord[]>(`${this.baseUrl}/medical-records/all`);
  }

  getMedicalRecordDetails(id: string): Observable<MedicalRecord> {
    return this.http.get<MedicalRecord>(`${this.baseUrl}/${id}`);
  }
  getDoctors(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8081/users/doctors');
  }

  generateNotesSummary(notesText: string): Observable<string> {
    const url = `${this.baseUrl}/summaries`;
    return this.http.post<{summary: string}>(url, { text: notesText }).pipe(
      map(response => response.summary)
    );
  }
}
