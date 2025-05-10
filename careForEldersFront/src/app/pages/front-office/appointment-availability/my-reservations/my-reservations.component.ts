import { Component, OnInit } from '@angular/core';
import { CalendarEvent, CalendarView } from 'angular-calendar';
import { AppointementService } from '../service/appointement-service/appointement.service';

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrl: './my-reservations.component.scss'
})
export class MyReservationsComponent implements OnInit{

  view: CalendarView = CalendarView.Week;
  CalendarView = CalendarView;
  viewDate: Date = new Date();
  events: CalendarEvent[] = [];
  loading = false;
   error: string | null = null;
constructor(
    private reservationService: AppointementService) {}



ngOnInit(): void {
    this.getUserInfo();
    this.loadReservationsFromBackend();

}
  getUserInfo(): void {
    // Get user ID directly from localStorage
    const userId = localStorage.getItem('user_id');
    console.log(userId);
    if (!userId) {
        console.warn('User ID not found in local storage.');
        return;
    }
    
}
loadReservationsFromBackend(): void {
    this.loading = true;
    this.error = null;

    // Get current user ID from local storage
    const userId = localStorage.getItem('user_id');
    
    if (!userId) {
      this.error = 'User not authenticated';
      this.loading = false;
      return;
    }

    this.reservationService.getReservationsByUserId(userId).subscribe({
      next: (reservations) => {
        this.events = reservations.map(reservation => ({
          start: new Date(reservation.date + 'T' + reservation.startTime),
          end: new Date(reservation.date + 'T' + reservation.endTime),
          title: `Reservation #${reservation.id} - ${reservation.serviceName}`,
          color: this.getStatusColor(reservation.status),
          meta: {
            reservation
          }
        }));
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load reservations';
        this.loading = false;
        console.error(err);
      }
    });
  }


  private getStatusColor(status: string): any {
    const colors: Record<string, any> = {
      confirmed: { primary: '#5cb85c', secondary: '#D1E7DD' },
      pending: { primary: '#f0ad4e', secondary: '#FFF3CD' },
      cancelled: { primary: '#d9534f', secondary: '#F8D7DA' },
      completed: { primary: '#5bc0de', secondary: '#CFF4FC' }
    };
    return colors[status.toLowerCase()] || { primary: '#777', secondary: '#EEE' };
  }

  setView(view: CalendarView): void {
    this.view = view;
  }

  eventClicked(event: CalendarEvent): void {
    console.log('Event clicked:', event.meta.reservation);
    // You can add a modal or other interaction here
  }
}
