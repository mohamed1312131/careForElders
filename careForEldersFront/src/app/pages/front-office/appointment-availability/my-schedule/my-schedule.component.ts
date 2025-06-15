import { Component, OnInit } from '@angular/core';
import { CalendarEvent, CalendarView } from 'angular-calendar';
import { DisponibiliteService } from '../service/disponibilite-service/disponibilite.service';
import { Router } from '@angular/router';
import { AppointementService } from '../service/appointement-service/appointement.service';
import { EventDetailsDialogComponent } from '../event-details-dialog/event-details-dialog.component';
import { MatDialog } from '@angular/material/dialog';

// Interfaces
interface AvailabilitySlot {
  id: string;
  date: string;
  heureDebut: string;
  heureFin: string;
  slotDuration: number;
  [key: string]: any;
}

interface Reservation {
  id: string;
  date: string;
  heure: string;
  endTime: string;
  statut: string;
  userId: string;
  doctorId: string;
  reservationType: string;
  meetingLink: string | null;
  location: string | null;
}

@Component({
  selector: 'app-my-schedule',
  templateUrl: './my-schedule.component.html',
  styleUrls: ['./my-schedule.component.scss']
})
export class MyScheduleComponent implements OnInit {
  view: CalendarView = CalendarView.Week;
  CalendarView = CalendarView;
  viewDate: Date = new Date();
  events: CalendarEvent[] = [];
  loading = false;
  userId: string | null = null;

  constructor(
    private disponibiliteService: DisponibiliteService,
    private appointementService: AppointementService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getUserInfo();
    if (this.userId) {
      this.loadScheduleData();
    }
  }

  getUserInfo(): void {
    this.userId = localStorage.getItem('user_id');
    console.log('User ID:', this.userId);
    if (!this.userId) {
      console.warn('User ID not found in local storage.');
    }
  }

  loadScheduleData(): void {
    if (!this.userId) return;

    this.loading = true;

    this.disponibiliteService.getByDoctor(this.userId).subscribe({
      next: (slots: AvailabilitySlot[]) => {
        this.appointementService.getReservationsByDoctorId(this.userId!).subscribe({
          next: (reservations: Reservation[]) => {
            const reservationEvents = reservations.map(res => this.mapReservationToEvent(res));

            const availabilityEvents: CalendarEvent[] = [];
            slots.forEach(slot => {
              const slotEvents = this.createTimeSlots(slot);
              slotEvents.forEach(availability => {
                const isOverlapping = reservationEvents.some(res =>
  res.end &&
  availability.end &&
  availability.start < res.end &&
  res.start < availability.end
                );
                if (!isOverlapping) {
                  availabilityEvents.push(availability);
                }
              });
            });

            // Final calendar events: reservations first, then available slots
            this.events = [...reservationEvents, ...availabilityEvents];
            console.log('Final Calendar Events:', this.events);
          },
          error: err => {
            console.error('Error loading reservations:', err);
          },
          complete: () => {
            this.loading = false;
          }
        });
      },
      error: err => {
        console.error('Error loading availability slots:', err);
        this.loading = false;
      }
    });
  }

  
// Add these methods to your MyScheduleComponent class

navigatePrevious(): void {
  if (this.view === CalendarView.Day) {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() - 1));
  } else if (this.view === CalendarView.Week) {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() - 7));
  } else if (this.view === CalendarView.Month) {
    this.viewDate = new Date(this.viewDate.setMonth(this.viewDate.getMonth() - 1));
  }
  this.loadScheduleData(); // Optional: reload data if needed
}

navigateNext(): void {
  if (this.view === CalendarView.Day) {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 1));
  } else if (this.view === CalendarView.Week) {
    this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 7));
  } else if (this.view === CalendarView.Month) {
    this.viewDate = new Date(this.viewDate.setMonth(this.viewDate.getMonth() + 1));
  }
  this.loadScheduleData(); // Optional: reload data if needed
}

navigateToday(): void {
  this.viewDate = new Date();
  this.loadScheduleData(); // Optional: reload data if needed
}


  private mapReservationToEvent(reservation: Reservation): CalendarEvent {
    const start = new Date(`${reservation.date}T${reservation.heure}`);
    const end = new Date(`${reservation.date}T${reservation.endTime}`);

    return {
      start: start,
      end: end,
      title: `Appointment - ${reservation.reservationType}`,
      color: {
        primary: '#d9534f',
        secondary: '#F8D7DA'
      },
      meta: {
        type: 'reservation',
        data: reservation,
        status: reservation.statut,
        meetingLink: reservation.meetingLink
      }
    };
  }

  private createTimeSlots(slot: AvailabilitySlot): CalendarEvent[] {
    const slots: CalendarEvent[] = [];
    const start = new Date(`${slot.date}T${slot.heureDebut}`);
    const end = new Date(`${slot.date}T${slot.heureFin}`);
    const duration = slot.slotDuration;

    let currentStart = new Date(start);

    while (currentStart < end) {
      const currentEnd = new Date(currentStart);
      currentEnd.setMinutes(currentEnd.getMinutes() + duration);

      if (currentEnd > end) break;

      slots.push({
        start: new Date(currentStart),
        end: new Date(currentEnd),
        title: `Available (${duration} min)`,
        color: { primary: '#5cb85c', secondary: '#D1E7DD' },
        meta: {
          type: 'availability',
          data: slot,
          slotDuration: duration
        }
      });

      currentStart = new Date(currentEnd);
    }

    return slots;
  }

  setView(view: CalendarView): void {
    this.view = view;
  }

  eventClicked({ event }: { event: CalendarEvent }): void {
  this.dialog.open(EventDetailsDialogComponent, {
    width: '400px',
    data: {
    ...event,              // this sends the calendar event data
    fromMySchedule: true   // this tells the dialog it's opened from MySchedule
  }
  });
  }

  onAddAvailability(): void {
    if (!this.userId) {
      console.error('Cannot add availability - User ID is missing');
      return;
    }
    this.router.navigate([`user/userProfile/doctor/${this.userId}/AddAvailability`]);
  }
}
