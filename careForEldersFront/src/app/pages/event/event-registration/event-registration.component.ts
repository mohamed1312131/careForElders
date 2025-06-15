import { Component, OnInit } from '@angular/core';
import { EventService } from 'src/app/services/event.service';
import { Event } from 'src/app/models/Event';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-event-registration',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './event-registration.component.html',
  styleUrls: ['./event-registration.component.scss']
})
export class EventRegistrationComponent implements OnInit {
  events: Event[] = [];
  currentUser: any = null;
  isLoading = false;
  registrationSuccess = false;
  errorMessage = '';
  isLoggedIn = false;
   alreadyRegistered = false;
  constructor(private eventService: EventService,private router:Router) {    
    this.isLoggedIn = !!localStorage.getItem('token'); }

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadEvents();
  }
 logout() {
    localStorage.removeItem('token'); // Or your auth token name
    this.isLoggedIn = false;
    this.router.navigate(['']);
  }
loadCurrentUser(): void {
  const userData = localStorage.getItem('user');
  if (userData) {
    try {
      this.currentUser = JSON.parse(userData);
      console.log('Current User:', this.currentUser); // Add this for debugging
    } catch (e) {
      console.error('Error parsing user data', e);
    }
  }
}

  loadEvents(): void {
    this.isLoading = true;
    this.eventService.getAllEvents().subscribe({
      next: (events) => {
        this.events = events;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load events';
        this.isLoading = false;
      }
    });
  }

onRegister(eventId: string | undefined): void {
  // 1. If not logged in → Redirect to login
  if (!this.isLoggedIn) {
    this.router.navigate(['/admin/authentication/login']);
    return;
  }

  // 2. If logged in but not SOIGNANT/USER → Show error
  if (!this.isSoignant()) {
    this.errorMessage = "You don't have permission to register for this event.";
    return;
  }

  // 3. If event ID missing → Show error
  if (!eventId) {
    this.errorMessage = 'Event ID is missing';
    return;
  }

  // 4. If already registered → Show info
  const event = this.events.find(e => e.id === eventId);
  if (event && event.participantIds?.includes(this.currentUser.id)) {
    this.alreadyRegistered = true;
    this.registrationSuccess = false;
    this.errorMessage = '';
    return;
  }

  // 5. Proceed with registration
  this.isLoading = true;
  this.registrationSuccess = false;
  this.alreadyRegistered = false;
  this.errorMessage = '';

  this.eventService.registerForEvent(eventId, this.currentUser.id).subscribe({
    next: (updatedEvent) => {
      const index = this.events.findIndex(e => e.id === eventId);
      if (index !== -1) {
        this.events[index] = updatedEvent;
      }
      this.registrationSuccess = true;
      this.isLoading = false;
    },
    error: (err) => {
      this.errorMessage = err.message || 'Registration failed';
      this.isLoading = false;
    }
  });
}
isSoignant(): boolean {
  return this.currentUser?.roles === 'SOIGNANT' || this.currentUser?.roles === 'USER';
}
}