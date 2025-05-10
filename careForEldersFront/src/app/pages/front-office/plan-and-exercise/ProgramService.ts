// program.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProgramService {
  private apiUrl = 'http://localhost:8091/api/programs';
  private apiUrl2 = 'http://localhost:8091/api/assignments'; // Assuming this is the correct URL for exercises
  private exerciseUrl = 'http://localhost:8091/api/exercises';

  constructor(private http: HttpClient) { }

  getProgramsByUserId(userId: string): Observable<any[]> {
    const headers = new HttpHeaders().set('X-User-ID', userId);
    return this.http.get<any[]>(`${this.apiUrl}/my-programs`, { headers });
  }
  getProgramDetailsWithProgress(programId: string, userId: string): Observable<any> {
    const headers = new HttpHeaders().set('X-User-ID', userId);
    return this.http.get<any>(
      `${this.apiUrl}/${programId}/details`,
      { headers }
    );
  }
  getDayExercises(assignmentId: string, dayNumber: number, userId: string): Observable<any> {
    const headers = new HttpHeaders().set('X-User-ID', userId);
    return this.http.get<any>(
      `${this.apiUrl2}/${assignmentId}/days/${dayNumber}`,
      { headers }
    );
  }
  
  // Complete day
  completeDay(assignmentId: string, dayNumber: number, userId: string): Observable<any> {
    const headers = new HttpHeaders().set('X-User-ID', userId);
    return this.http.post(
      `${this.apiUrl2}/${assignmentId}/complete-day`, 
      { dayNumber },
      { headers }
    );
  }
  startDay(assignmentId: string, dayNumber: number, userId: string): Observable<any> {
    const headers = new HttpHeaders().set('X-User-ID', userId);
    return this.http.post(
      `${this.apiUrl2}/${assignmentId}/start-day`,
      { dayNumber },
      { headers }
    );
  }
  createProgram(programDto: any, doctorId: string): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/create`,
      programDto,
      { headers: new HttpHeaders().set('X-User-ID', doctorId) }
    );
  }

  getAllExercises(): Observable<any[]> {
    return this.http.get<any[]>(`${this.exerciseUrl}/getAllExercises`);
  }
  getPrograms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/getAllPrograms`);
  }
  
  deleteProgram(programId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deleteById/${programId}`);
  }

  getProgramDetails(programId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/getProgramDetails/${programId}`);
  }
  
  getProgramPatients(programId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/getPatients/${programId}/patients`);
  }
  createExercise(
    exerciseData: any, // This will be your ExerciseDTO content
    imageFile: File | null,
    videoFile: File | null,
    doctorId: string
  ): Observable<any> {
    const formData = new FormData();

    // Append exerciseDTO as a JSON string blob
    // The backend expects a part named "exerciseDTO"
    formData.append('exerciseDTO', new Blob([JSON.stringify(exerciseData)], { type: 'application/json' }));

    if (imageFile) {
      formData.append('imageFile', imageFile, imageFile.name);
    }
    if (videoFile) {
      formData.append('videoFile', videoFile, videoFile.name);
    }

    const headers = new HttpHeaders().set('X-User-ID', doctorId);
    // When sending FormData, HttpClient sets the Content-Type to multipart/form-data automatically,
    // including the boundary. So, we don't explicitly set Content-Type here.

    return this.http.post<any>(
      `${this.exerciseUrl}/create`,
      formData, // Send formData as the body
      { headers }
    );
  }

}