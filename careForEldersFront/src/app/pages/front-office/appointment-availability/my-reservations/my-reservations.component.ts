import { Component, OnInit } from '@angular/core';
import { CalendarEvent, CalendarView } from 'angular-calendar';
import { AppointementService } from '../service/appointement-service/appointement.service';
import { EventDetailsDialogComponent } from '../event-details-dialog/event-details-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrls: ['./my-reservations.component.scss']
})
export class MyReservationsComponent implements OnInit {
  view: CalendarView = CalendarView.Week;
  CalendarView = CalendarView;
  viewDate: Date = new Date();
  events: CalendarEvent[] = [];
  loading = false;
  error: string | null = null;
  

  constructor(private reservationService: AppointementService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.getUserInfo();
    this.loadReservationsFromBackend();
  }

  getUserInfo(): void {
    const userId = localStorage.getItem('user_id');
    console.log('User ID:', userId);
    if (!userId) {
      console.warn('User ID not found in local storage.');
      return;
    }
  }

  loadReservationsFromBackend(): void {
    this.loading = true;
    this.error = null;

    const userId = localStorage.getItem('user_id');
    if (!userId) {
      this.error = 'User not authenticated';
      this.loading = false;
      return;
    }

    this.reservationService.getReservationsByUserId(userId).subscribe({
      next: (reservations) => {
        this.events = reservations.map(reservation => this.mapReservationToEvent(reservation));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching reservations:', err);
        this.error = 'Failed to load reservations';
        this.loading = false;
      }
    });
  }

  private mapReservationToEvent(reservation: any): CalendarEvent {
  const startTime = reservation.heure || '00:00:00';
  const status = reservation.statut || 'pending';

  const start = new Date(`${reservation.date}T${startTime}`);
  let end: Date;
  if (reservation.endTime) {
    end = new Date(`${reservation.date}T${reservation.endTime}`);
  } else {
    end = new Date(start);
    end.setHours(end.getHours() + 1);
  }

  // Determine if the event is in the past
  const isPast = start < new Date();

  return {
    start,
    end,
    title: this.generateEventTitle(reservation),
    color: isPast ? this.getPastEventColor() : this.getStatusColor(status),
    meta: {
      type: 'reservation',
      status: reservation.statut,
      meetingLink: reservation.meetingLink,
      data: reservation
    }
  };


  }
  private getPastEventColor(): any {
  return {
    primary: '#d9534f',   // Bootstrap danger red
    secondary: '#F8D7DA'  // Light red
  };
}

  private generateEventTitle(reservation: any): string {
    const type = reservation.reservationType === 'PRESENTIEL' ? 'In-person' : 'Online';
    return `Appointment (${type}) at ${reservation.location || 'Unknown location'}`;
  }

  private getStatusColor(status: string): any {
    const statusMap: Record<string, string> = {
      'planifiee': 'pending',
      'confirmee': 'confirmed',
      'annulee': 'cancelled',
      'terminee': 'completed'
    };

    // Map French status to English equivalents
    const englishStatus = statusMap[status.toLowerCase()] || status.toLowerCase();

    const colors: Record<string, any> = {
      confirmed: { primary: '#5cb85c', secondary: '#D1E7DD' },
      pending: { primary: '#f0ad4e', secondary: '#FFF3CD' },
      cancelled: { primary: '#d9534f', secondary: '#F8D7DA' },
      completed: { primary: '#5bc0de', secondary: '#CFF4FC' },
      default: { primary: '#777', secondary: '#EEE' }
    };

    return colors[englishStatus] || colors;
  }

  setView(view: CalendarView): void {
    this.view = view;
  }

   eventClicked({ event }: { event: CalendarEvent }): void {
    console.log('event clicked !', event);
    this.dialog.open(EventDetailsDialogComponent, {
      width: '400px',
      data: event
    });
    }

    // Add these navigation methods
      navigatePrevious(): void {
        if (this.view === CalendarView.Day) {
          this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() - 1));
        } else if (this.view === CalendarView.Week) {
          this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() - 7));
        } else if (this.view === CalendarView.Month) {
          this.viewDate = new Date(this.viewDate.setMonth(this.viewDate.getMonth() - 1));
        }
        
      }
    
      navigateNext(): void {
        if (this.view === CalendarView.Day) {
          this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 1));
        } else if (this.view === CalendarView.Week) {
          this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 7));
        } else if (this.view === CalendarView.Month) {
          this.viewDate = new Date(this.viewDate.setMonth(this.viewDate.getMonth() + 1));
        }
}
navigateToday(): void {
    this.viewDate = new Date();
}
}