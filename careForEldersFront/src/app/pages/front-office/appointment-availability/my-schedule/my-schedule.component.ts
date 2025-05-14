import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { CalendarEvent, CalendarView } from 'angular-calendar';

@Component({
  selector: 'app-my-schedule',
  templateUrl: './my-schedule.component.html',
  styleUrl: './my-schedule.component.scss'
})
export class MyScheduleComponent implements OnInit {
  view: CalendarView = CalendarView.Week;
  CalendarView = CalendarView;
  viewDate: Date = new Date();
  events: CalendarEvent[] = [];
  doctor: any;
  loading = false;
  doctorId: string | null = null;

  constructor(private router: Router, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.logLocalStorageContents();
    this.doctorId = this.getDoctorIdFromLocalStorage();
    this.loadScheduleFromLocalStorage();
  }

  private logLocalStorageContents(): void {
    console.log('LocalStorage contents:');
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key) {
        console.log(`Key: ${key}, Value:`, localStorage.getItem(key));
      }
    }
  }

  private getDoctorIdFromLocalStorage(): string | null {
    try {
      const professionalString = localStorage.getItem('currentProfessional');
      if (!professionalString) {
        console.warn('No professional data found in localStorage');
        return null;
      }
      
      const professional = JSON.parse(professionalString);
      const doctorId = professional?.id || null;
      
      console.log('Retrieved Doctor ID:', doctorId);
      return doctorId;
      
    } catch (error) {
      console.error('Error parsing professional data from localStorage:', error);
      return null;
    }
  }

  loadScheduleFromLocalStorage(): void {
    this.loading = true;
    
    setTimeout(() => {
      try {
        const userData = localStorage.getItem('currentProfessional');
        if (!userData) {
          console.warn('No professional data found in local storage');
          return;
        }

        const currentProfessional = JSON.parse(userData);
        const scheduleSlots = currentProfessional.schedule || [];
        const appointments = currentProfessional.appointments || [];

        // Convert schedule slots to events
        this.events = [
          ...scheduleSlots.map((slot: any) => ({
            start: new Date(slot.date + 'T' + slot.startTime),
            end: new Date(slot.date + 'T' + slot.endTime),
            title: 'Available',
            color: { primary: '#5cb85c', secondary: '#D1E7DD' },
            meta: { type: 'availability', data: slot }
          })),
          ...appointments.map((appointment: any) => ({
            start: new Date(appointment.date + 'T' + appointment.startTime),
            end: new Date(appointment.date + 'T' + appointment.endTime),
            title: `Appointment with ${appointment.patientName}`,
            color: this.getAppointmentColor(appointment.status),
            meta: { type: 'appointment', data: appointment }
          }))
        ];

      } catch (error) {
        console.error('Error loading schedule:', error);
      } finally {
        this.loading = false;
      }
    }, 500);
  }

  private getAppointmentColor(status: string): any {
    const colors: Record<string, any> = {
      confirmed: { primary: '#0275d8', secondary: '#D9EDF7' },
      completed: { primary: '#5cb85c', secondary: '#DFF0D8' },
      cancelled: { primary: '#d9534f', secondary: '#F2DEDE' },
      noshow: { primary: '#f0ad4e', secondary: '#FCF8E3' }
    };
    return colors[status.toLowerCase()] || { primary: '#777', secondary: '#EEE' };
  }

  setView(view: CalendarView): void {
    this.view = view;
  }

  eventClicked(event: CalendarEvent): void {
    if (event.meta.type === 'appointment') {
      console.log('Appointment clicked:', event.meta.data);
      // Show appointment details
    } else {
      console.log('Availability slot clicked:', event.meta.data);
      // Show availability details or edit form
    }
  }

  onAddAvailability(): void {
    console.log('Current Doctor ID when clicking Add Availability:', this.doctorId);
    
    if (!this.doctorId) {
      this.snackBar.open('Doctor ID is missing. Please log in again.', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    // Navigate to the "Add Availability" page using the stored doctor ID
    this.router.navigate([`user/userProfile/doctor/${this.doctorId}/AddAvailability`]);
  }
}