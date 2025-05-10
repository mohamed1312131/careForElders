import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class CareService {
  private apiUrl = 'http://localhost:8086/api/services';

  constructor(private http: HttpClient) {}

  getActiveServices(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/offerings`);
  }

createServiceRequest(request: any): Observable<any> {
    const body = {
        userId: localStorage.getItem('userId'),  // Get from auth
        serviceOfferingId: request.serviceId,
        specialInstructions: request.specialInstructions,
        requiredDurationHours: request.requiredDurationHours
    };
    return this.http.post(`${this.apiUrl}/requests`, body);
}

  getPendingRequests(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/requests?status=PENDING`);
  }

  assignRequest(requestId: string, soignantId: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/requests/${requestId}/assign`, { soignantId });
  }
getUserRequests(userId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/requests/user/${userId}`);
}
  getDoctorServices(doctorId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/offerings/doctor/${doctorId}`);
  }

  getServiceDetails(serviceId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/offerings/${serviceId}/details`);
  }
  updateStatus(requestId: string, newStatus: string): Observable<any> {
    const currentUserId = localStorage.getItem('userId'); // Get from auth service
    return this.http.patch(`${this.apiUrl}/requests/${requestId}/status`, 
        { newStatus },
        { headers: { 'X-Current-User': currentUserId || '' } }
    );
}
getDoctorRequests(doctorId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/requests/doctor/${doctorId}`);
}
}