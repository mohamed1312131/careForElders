// program.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
export interface ProgramAssignmentDTO {
  programId: string;
  patientId: string;

}
@Injectable({
  providedIn: 'root'
})
export class ProgramService {
  private apiUrl = 'http://localhost:8091/api/programs';
  private apiUrl2 = 'http://localhost:8091/api/assignments'; // Assuming this is the correct URL for exercises
  private exerciseUrl = 'http://localhost:8091/api/exercises';
    private userServiceUrl = 'http://localhost:8081/users';

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
    `${this.apiUrl2}/${assignmentId}/start-day/${dayNumber}`,
    {}, // empty body
    { headers }
  );
}
  createProgram(programDto: any, imageFile: File | null, doctorId: string): Observable<any> {
  const formData = new FormData();
  
  // Append program data as JSON blob
  formData.append('program', new Blob([JSON.stringify(programDto)], {
    type: 'application/json'
  }));
  
  // Append image file if exists
  if (imageFile) {
    formData.append('image', imageFile);
  }
  
  const headers = new HttpHeaders().set('X-User-ID', doctorId);
  
  return this.http.post<any>(
    `${this.apiUrl}/create`,
    formData,
    { headers }
  );
}

  getAllExercises(): Observable<any[]> {
    return this.http.get<any[]>(`${this.exerciseUrl}/getAllExercises`);
  }
  getPrograms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/getAllPrograms`);
  }
  
deleteProgram(programId: string, doctorId: string): Observable<void> {
  const headers = new HttpHeaders().set('X-User-ID', doctorId);
  return this.http.delete<void>(`${this.apiUrl}/deleteById/${programId}`, { headers });
}

unassignProgramFromPatient(programId: string, patientId: string, doctorId: string): Observable<any> {
  return this.http.delete(`${this.apiUrl}/programs/${programId}/patients/${patientId}`, {
    headers: new HttpHeaders({ 'doctor-id': doctorId })
  });
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
  assignProgramToPatient(assignmentDTO: ProgramAssignmentDTO, doctorId: string): Observable<any> {
    const headers = new HttpHeaders().set('X-User-ID', doctorId);
    return this.http.post<any>(this.apiUrl2, assignmentDTO, { headers });
  }
  updateProgram(programId: string, programData: any, doctorId: string): Observable<any> {
    const headers = new HttpHeaders().set('X-User-ID', doctorId);

    return this.http.put<any>(`${this.apiUrl}/update/${programId}`, programData, { headers }); 
    
  }

  getAllAssignablePatients(): Observable<any[]> {
    // Example: Assuming user service returns users with 'NORMAL_USER' role at this endpoint.
    // You might need to pass a doctorId header if your user service requires it for authorization.
    // const headers = new HttpHeaders().set('X-User-ID', doctorId); // If needed
    return this.http.get<any[]>(`${this.userServiceUrl}/role/USER`);
    // Alternative: Fetch all users and filter by role on the frontend if the API doesn't support role filtering.
    // return this.http.get<any[]>(`${this.userServiceUrl}`).pipe(
    //   map(users => users.filter(user => user.role === 'NORMAL_USER'))
    // );
  }
  updateProgramDay(programId: string, dayId: string, dayData: any, doctorId: string): Observable<any> {
  const headers = new HttpHeaders().set('X-User-ID', doctorId);
  return this.http.put<any>(
    `${this.apiUrl}/${programId}/days/${dayId}`,
    dayData,
    { headers }
  );
}

deleteProgramDay(programId: string, dayId: string, doctorId: string): Observable<void> {
  const headers = new HttpHeaders().set('X-User-ID', doctorId);
  return this.http.delete<void>(
    `${this.apiUrl}/${programId}/days/${dayId}`,
    { headers }
  );
}
addDayToProgram(programId: string, dayData: any, doctorId: string): Observable<any> {
  const headers = new HttpHeaders().set('X-User-ID', doctorId);
  return this.http.post<any>(
    `${this.apiUrl}/${programId}/days`,
    dayData,
    { headers }
  );
}
getUnassignedPatientsForProgram(programId: string): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/${programId}/unassigned-patients`);
}
getRecommendedPrograms(userId: string): Observable<any[]> {
  const headers = new HttpHeaders().set('X-User-ID', userId);
  return this.http.get<any[]>(`${this.apiUrl}/recommendations`, { headers });
}
selfAssignProgram(programId: string, userId: string): Observable<any> {
  const headers = new HttpHeaders().set('X-User-ID', userId);
  return this.http.post<any>(
    `${this.apiUrl2}/self-assign?programId=${programId}`,
    {}, // empty body since we're using query param
    { headers }
  );
}
getProgramStatistics(programId: string): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/${programId}/statistics`);
}
getPatientAssignmentDetails(programId: string, patientId: string): Observable<any> {
  return this.http.get<any>(
    `${this.apiUrl2}/program/${programId}/patient/${patientId}`
  );
}
}