import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AbonnementService {

  private apiUrl = 'http://localhost:8092/api/subscriptions';

  constructor(private http: HttpClient) { }

  getSubscriptionPlans(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/plans`);
  }

  subscribeUser(userId: string, planId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/user-subscriptions`, { userId, planId });
  }
  getCurrentSubscription(userId: string): Observable<any> {
  return this.http.get(`${this.apiUrl}/user/${userId}`);
}
}
