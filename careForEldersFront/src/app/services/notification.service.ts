// notification.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, interval } from 'rxjs';
import { switchMap, startWith, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8088'; // Notification service URL
  private notifications: any[] = [];
  private hasUnreadNotifications = false;

  constructor(private http: HttpClient) {}

  getUserNotifications(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/notifications/user/${userId}`);
  }

// In notification.service.ts
respondToNotification(notificationId: string, userId: string, location?: any): Observable<any> {
  const payload = location ? { 
    location: {
      latitude: location.latitude,
      longitude: location.longitude,
      accuracy: location.accuracy
    }
  } : {};
  
  return this.http.post(
    `${this.apiUrl}/notifications/${notificationId}/respond/${userId}`,
    payload
  ).pipe(
    tap(() => {
      this.notifications = this.notifications.filter(n => n.id !== notificationId);
    })
  );
}

  startPolling(userId: string): Observable<any> {
    return interval(10000) // Poll every 10 seconds
      .pipe(
        startWith(0), // Start immediately
        switchMap(() => this.getUserNotifications(userId))
      );
  }

  getNotifications(): any[] {
    return this.notifications;
  }

  hasUnread(): boolean {
    return this.hasUnreadNotifications;
  }
}