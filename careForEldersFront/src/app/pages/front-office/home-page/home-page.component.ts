import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs';
import { Notification } from 'src/app/models/Notification';
import { AuthService } from 'src/app/services/auth.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  userId: string | null = null;
  showNotifications = false;
userRole: string | null = null;
  unreadCount = 0;
  private notificationSub!: Subscription;
 notifications: Notification[] = []; 
  constructor(
    private authService: AuthService,
    private router : Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.checkLoginStatus();
    this.setupNotificationPolling();
  }

  ngOnDestroy(): void {
    if (this.notificationSub) {
      this.notificationSub.unsubscribe();
    }
  }

checkLoginStatus(): void {
  this.isLoggedIn = !!localStorage.getItem('token');
  
  // Get the user object from localStorage
  const userString = localStorage.getItem('user');
  if (userString) {
    try {
      const user = JSON.parse(userString);
      // Get user ID from the user object
      this.userId = user.id || null;
      // Check both 'role' and 'roles' to be safe
      this.userRole = user.role || user.roles || null;
      
      console.log('User ID:', this.userId); // For debugging
      console.log('User Role:', this.userRole); // For debugging
    } catch (e) {
      console.error('Error parsing user data:', e);
      this.userId = null;
      this.userRole = null;
    }
  } else {
    this.userId = null;
    this.userRole = null;
  }
}

  setupNotificationPolling(): void {
    this.notificationSub = this.notificationService.startPolling(this.userId || '').subscribe({
      next: (notifications: Notification[]) => { // Add type here
        this.notifications = notifications;
        this.unreadCount = notifications.filter((n: Notification) => !n.responded && n.active).length;
      },
      error: (err) => console.error('Error fetching notifications:', err)
    });
  }


  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

async respondToNotification(notificationId: string): Promise<void> {
  if (!this.userId) return;

  try {
    // Get user's current location
    const position = await this.getCurrentPosition();
    const location = {
      latitude: position.coords.latitude,
      longitude: position.coords.longitude,
      accuracy: position.coords.accuracy,
      timestamp: new Date(position.timestamp)
    };

    this.notificationService.respondToNotification(
      notificationId, 
      this.userId,
      location
    ).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== notificationId);
        this.unreadCount = this.notifications.filter(n => !n.responded && n.active).length;
      },
      error: (err) => console.error('Error responding:', err)
    });
  } catch (error) {
    console.warn('Could not get location, sending without it:', error);
    // Fallback without location
    this.notificationService.respondToNotification(notificationId, this.userId).subscribe();
  }
}

private getCurrentPosition(): Promise<GeolocationPosition> {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject('Geolocation not supported');
      return;
    }

    navigator.geolocation.getCurrentPosition(
      position => resolve(position),
      error => reject(error),
      { 
        enableHighAccuracy: true,
        timeout: 5000,
        maximumAge: 0
      }
    );
  });
}


  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user_id');
    this.isLoggedIn = false;
    this.router.navigate(['']);
        this.notifications = [];
    this.unreadCount = 0;
  }


}