import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { CalendarEvent, CalendarView } from 'angular-calendar';
import { AppointmentDialogComponent } from '../appointment-dialog/appointment-dialog.component';
import { DisponibiliteService } from '../service/disponibilite-service/disponibilite.service';
import { AppointementService } from '../service/appointement-service/appointement.service';
import { MatSnackBar } from '@angular/material/snack-bar';

interface Availability {
  id: string;
  doctorId: string;
  date: string | null;
  heureDebut: string;
  heureFin: string;
  slotDuration: number;
  valid: boolean;
}

interface Reservation {
  id: string;
  date: string;
  heure: string;
  endTime: string;
  statut: string;
  [key: string]: any;
}

@Component({
  selector: 'app-doctor-details',
  templateUrl: './doctor-details.component.html',
  styleUrls: ['./doctor-details.component.scss']
})
export class DoctorDetailsComponent implements OnInit {
  isDoctor: boolean = true;
  isLoading: boolean = false;
  error: string | null = null;
  doctor: any;

  view: CalendarView = CalendarView.Month;
  viewDate: Date = new Date();
  CalendarView = CalendarView;

  events: CalendarEvent[] = [];
  reservations: Reservation[] = [];

  dayViewStart = { hour: 8, minute: 0 };
  dayEndStart = { hour: 20, minute: 0 };
  hourSegmentHeight = 60;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private disponibiliteService: DisponibiliteService,
    private doctorService: AppointementService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const doctorId = this.route.snapshot.paramMap.get('id');
    if (doctorId) {
      this.loadDoctorDetails(doctorId);
      this.loadDoctorReservations(doctorId);
      this.loadDoctorAvailability(doctorId);
    }
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
    this.loadDoctorAvailability(this.doctor.id);
  }

  navigateNext(): void {
    if (this.view === CalendarView.Day) {
      this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 1));
    } else if (this.view === CalendarView.Week) {
      this.viewDate = new Date(this.viewDate.setDate(this.viewDate.getDate() + 7));
    } else if (this.view === CalendarView.Month) {
      this.viewDate = new Date(this.viewDate.setMonth(this.viewDate.getMonth() + 1));
    }
    this.loadDoctorAvailability(this.doctor.id);
  }

  navigateToday(): void {
    this.viewDate = new Date();
    this.loadDoctorAvailability(this.doctor.id);
  }

  loadDoctorDetails(doctorId: string): void {
    this.doctorService.getDoctorById(doctorId).subscribe({
      next: (doctor) => this.doctor = doctor,
      error: (err) => {
        console.error('Error loading doctor:', err);
        this.error = 'Failed to load doctor details.';
      }
    });
  }

  loadDoctorReservations(doctorId: string): void {
    this.doctorService.getReservationsByDoctorId(doctorId).subscribe({
      next: (reservations: Reservation[]) => {
        console.log('Reservations loaded:', reservations);
        this.reservations = reservations;
        if (this.events.length > 0) {
          this.updateEventsWithReservations();
        }
      },
      error: (err) => {
        console.error('Error loading reservations:', err);
        this.snackBar.open('Failed to load reservations', 'Close', {
          duration: 3000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  loadDoctorAvailability(doctorId: string): void {
    this.disponibiliteService.getByDoctor(doctorId).subscribe({
      next: (availabilities: Availability[]) => {
        console.log('Availabilities loaded:', availabilities);
        this.events = this.mapAvailabilitiesToEvents(availabilities);
        if (this.reservations.length > 0) {
          this.updateEventsWithReservations();
        }
      },
      error: (err) => {
        console.error('Error loading availability:', err);
        this.error = 'Failed to load availability.';
      }
    });
  }

  private updateEventsWithReservations(): void {
    this.events = this.events.map(event => {
      if (!event.start || !event.end) {
        console.warn('Event missing start or end date:', event);
        return event;
      }

      const isBooked = this.isSlotBooked(event.start, event.end);
      const isPast = event.end < new Date();
      
      return {
        ...event,
        title: isBooked ? 'Booked' : `Available (${event.meta.slotDuration}min)`,
        color: (isBooked || isPast)
          ? { primary: '#dc3545', secondary: '#f8d7da' } // red for both booked and past
          : { primary: '#1e90ff', secondary: '#D1E8FF' }, // blue for available
        meta: {
          ...event.meta,
          isBooked
        }
      };
    });
  }

  private isSlotBooked(start: Date, end: Date): boolean {
    if (!(start instanceof Date)) {
      console.error('Invalid start date:', start);
      return false;
    }
    if (!(end instanceof Date)) {
      console.error('Invalid end date:', end);
      return false;
    }

    return this.reservations.some(reservation => {
      const reservationStart = this.combineDateAndTime(reservation.date, reservation.heure);
      const reservationEnd = this.combineDateAndTime(reservation.date, reservation.endTime);
      
      if (!reservationStart || !reservationEnd) return false;

      return (
        (start >= reservationStart && start < reservationEnd) ||
        (end > reservationStart && end <= reservationEnd) ||
        (start <= reservationStart && end >= reservationEnd)
      );
    });
  }

  private combineDateAndTime(dateStr: string, timeStr: string): Date | null {
    if (!dateStr || !timeStr) return null;
    const datetimeStr = `${dateStr}T${timeStr}`;
    const date = new Date(datetimeStr);
    return isNaN(date.getTime()) ? null : date;
  }

  private mapAvailabilitiesToEvents(availabilities: Availability[]): CalendarEvent[] {
    return availabilities
      .filter(avail => avail.valid && avail.date && avail.heureDebut && avail.heureFin)
      .flatMap(avail => {
        try {
          return this.createTimeSlots(
            avail.date!,
            avail.heureDebut,
            avail.heureFin,
            avail.slotDuration,
            avail.id
          );
        } catch (error) {
          console.error('Error creating slots for availability:', avail, error);
          return [];
        }
      });
  }

  private createTimeSlots(
    dateStr: string,
    startTime: string,
    endTime: string,
    durationMinutes: number,
    availabilityId: string
  ): CalendarEvent[] {
    const slots: CalendarEvent[] = [];

    try {
      const [startHour, startMinute] = startTime.split(':').map(Number);
      const [endHour, endMinute] = endTime.split(':').map(Number);

      const startDate = new Date(dateStr);
      startDate.setHours(startHour, startMinute, 0, 0);

      const endDate = new Date(dateStr);
      endDate.setHours(endHour, endMinute, 0, 0);

      if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
        throw new Error('Invalid date created');
      }

      let currentSlotStart = new Date(startDate);
      const now = new Date();

      while (currentSlotStart < endDate) {
        const currentSlotEnd = new Date(currentSlotStart);
        currentSlotEnd.setMinutes(currentSlotEnd.getMinutes() + durationMinutes);

        if (currentSlotEnd > endDate) break;

        const isPast = currentSlotEnd < now;

        slots.push({
          start: new Date(currentSlotStart),
          end: new Date(currentSlotEnd),
          title: `Available (${durationMinutes}min)`,
          color: isPast
            ? { primary: '#dc3545', secondary: '#f8d7da' }
            : { primary: '#1e90ff', secondary: '#D1E8FF' },
          meta: {
            availabilityId,
            slotDuration: durationMinutes,
            originalStart: startTime,
            originalEnd: endTime,
            isBooked: false
          }
        });

        currentSlotStart = new Date(currentSlotEnd);
      }
    } catch (error) {
      console.error('Error creating time slots:', error);
    }

    return slots;
  }

  handleDayClick(event: { date?: Date; day?: { date: Date } }): void {
    const selectedDate = event.date || event.day?.date;
    if (selectedDate) {
      this.viewDate = selectedDate;
      this.view = CalendarView.Day;
    }
  }

  handleEventClick(event: any): void {
  const clickedEvent = event.event || event;
  const now = new Date();
  const isPast = clickedEvent.start < now;

  if (isPast) {
    this.snackBar.open("You can't take an appointment in the past.", 'Close', {
      duration: 3000,
      panelClass: ['snackbar-warning']
    });
    return;
  }

  if (clickedEvent.meta.isBooked) {
    this.snackBar.open("This slot is already booked.", 'Close', {
      duration: 3000,
      panelClass: ['snackbar-warning']
    });
    return;
  }

  const dialogRef = this.dialog.open(AppointmentDialogComponent, {
    width: '500px',
    data: {
      doctor: this.doctor,
      availability: {
        id: clickedEvent.meta.availabilityId,
        slotDuration: clickedEvent.meta.slotDuration,
        originalStartTime: clickedEvent.meta.originalStart,
        originalEndTime: clickedEvent.meta.originalEnd
      },
      slot: {
        start: clickedEvent.start,
        end: clickedEvent.end,
        formattedTime: this.formatTimeRange(clickedEvent.start, clickedEvent.end)
      },
      doctorInfo: {
        id: this.doctor.id,
        name: `${this.doctor.firstName} ${this.doctor.lastName}`,
        specialty: this.doctor.specialty
      }
    }
  });

  // Add this subscription to handle the dialog closing
  dialogRef.afterClosed().subscribe(result => {
    if (result === 'confirmed') {
      // Reload the doctor's availability and reservations
      this.loadDoctorReservations(this.doctor.id);
      this.loadDoctorAvailability(this.doctor.id);
      
      // Optional: Show a success message
      this.snackBar.open('Appointment booked successfully!', 'Close', {
        duration: 3000,
        panelClass: ['snackbar-success']
      });
    }
  });
}

  private formatTimeRange(start: Date, end: Date): string {
    return `${this.formatTime(start)} - ${this.formatTime(end)}`;
  }

  private formatTime(date: Date): string {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
}