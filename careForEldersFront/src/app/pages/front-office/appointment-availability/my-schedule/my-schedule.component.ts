import { Component, OnInit } from '@angular/core';
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
  constructor(private router : Router){}
  ngOnInit(): void {

    
    this.loadScheduleFromLocalStorage();
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
  try {
    const professional = JSON.parse(localStorage.getItem('currentProfessional') || '{}');
    if (!professional?.id) throw new Error('Doctor ID not found');
    
    this.router.navigate([`user/userProfile/doctor/${professional.id}/AddAvailability`]);
  } catch (error) {
    console.error('Failed to navigate:', error);
    // Optionally show user feedback
  }
}
}
