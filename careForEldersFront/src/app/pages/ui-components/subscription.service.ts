import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface SubscriptionPlan {
  id: string;
  name: string;
  price: number;
  durationDays: number;
  features: string[];
}

export interface UserSubscription {
  userId: string;
  planId: string;
  planName: string;
  startDate: string;
  endDate: string;
  active: boolean;
  status: 'active' | 'expired' | 'pending'; // Add this
  price?: number;
  durationDays?: number;
  features?: string[];
}

export interface SubscriptionAnalytics {
  totalActiveSubscriptions: number;
  monthlyRevenue: number;
  mostPopularPlan: string;
  mostPopularPlanPercentage: number;
}

export interface SubscriptionPlanDTO {
  name: string;
  price: number;
  durationDays: number;
  features: string[];
}

export interface UserSubscriptionDTO {
  userId: string;
  planId: string;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = 'http://localhost:8092/api/subscriptions'; // Update with your actual backend URL

  constructor(private http: HttpClient) {}

  // Helper method to get headers with admin ID
  private getHeaders(adminId: string): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Admin-Id': adminId
    });
  }

  // Plan Management
  createPlan(adminId: string, plan: SubscriptionPlanDTO): Observable<SubscriptionPlan> {
    return this.http.post<SubscriptionPlan>(
      `${this.apiUrl}/plans`,
      plan,
      { headers: this.getHeaders(adminId) }
    ).pipe(
      catchError(this.handleError)
    );
  }

  getAllPlans(): Observable<SubscriptionPlan[]> {
    return this.http.get<SubscriptionPlan[]>(`${this.apiUrl}/plans`).pipe(
      catchError(this.handleError)
    );
  }

  updatePlan(adminId: string, planId: string, plan: SubscriptionPlanDTO): Observable<SubscriptionPlan> {
    return this.http.put<SubscriptionPlan>(
      `${this.apiUrl}/plans/${planId}`,
      plan,
      { headers: this.getHeaders(adminId) }
    ).pipe(
      catchError(this.handleError)
    );
  }

  deletePlan(adminId: string, planId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/plans/${planId}`,
      { headers: this.getHeaders(adminId) }
    ).pipe(
      catchError(this.handleError)
    );
  }

  // User Subscription Management
  assignPlanToUser(subscription: UserSubscriptionDTO): Observable<UserSubscription> {
    return this.http.post<UserSubscription>(
      `${this.apiUrl}/assign`,
      subscription
    ).pipe(
      catchError(this.handleError)
    );
  }

  assignDefaultPlan(userId: string): Observable<UserSubscription> {
    return this.http.post<UserSubscription>(
      `${this.apiUrl}/assign-default/${userId}`,
      {}
    ).pipe(
      catchError(this.handleError)
    );
  }

  getSubscriptionsByUser(userId: string): Observable<UserSubscription[]> {
    return this.http.get<UserSubscription[]>(`${this.apiUrl}/user/${userId}`).pipe(
      catchError(this.handleError)
    );
  }

  /*getCurrentSubscriptionForUser(userId: string): Observable<UserSubscription | null> {
    return this.http.get<UserSubscription>(`${this.apiUrl}/current/${userId}`).pipe(
      catchError(error => {
        if (error.status === 404) {
          return of(null);
        }
        return throwError(error);
      })
    );
  }*/

  // Analytics (you'll need to implement this endpoint in your backend)
  getAnalytics(adminId: string): Observable<SubscriptionAnalytics> {
    return this.http.get<SubscriptionAnalytics>(
      `${this.apiUrl}/analytics`,
      { headers: this.getHeaders(adminId) }
    ).pipe(
      catchError(this.handleError)
    );
  }

  // Error handling
  private handleError(error: any) {
    console.error('An error occurred:', error);
    return throwError(() => new Error(
      error.error?.message || error.message || 'Server error'
    ));
  }

  // Add these to your SubscriptionService class

getPlans(): Observable<SubscriptionPlan[]> {
  return this.getAllPlans();
}

getUserSubscriptions(searchTerm: string): Observable<UserSubscription[]> {
  // Implement this based on your backend API
  return this.http.get<UserSubscription[]>(`${this.apiUrl}/users?search=${searchTerm}`);
}

}