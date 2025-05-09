import { Component, OnInit } from '@angular/core';
import { CalendarEvent, CalendarModule, CalendarMonthViewDay, CalendarView } from 'angular-calendar';
import { addDays, setHours, setMinutes } from 'date-fns';

import { CommonModule } from '@angular/common';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { ActivatedRoute, Router } from '@angular/router';
import { DisponibiliteService } from '../service/disponibilite-service/disponibilite.service';
import { AppointementService } from '../service/appointement-service/appointement.service';
import { MatDialog } from '@angular/material/dialog';
import { AppointmentDialogComponent } from '../appointment-dialog/appointment-dialog.component';

@Component({
  selector: 'app-doctor-details',
  templateUrl: './doctor-details.component.html',
  styleUrls: ['./doctor-details.component.scss']
})
export class DoctorDetailsComponent implements OnInit {
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private disponibiliteService: DisponibiliteService,
    private doctorService: AppointementService,
    public dialog: MatDialog
  ) {}

  isDoctor: boolean = true;
  doctor: any;
  view: CalendarView = CalendarView.Month;
  viewDate: Date = new Date();
  CalendarView = CalendarView;
  events: CalendarEvent[] = [];

  // Calendar day view settings (8am to 8pm)
  dayViewStart = { hour: 8, minute: 0 };
  dayEndStart = { hour: 20, minute: 0 };
  hourSegmentHeight = 60;

  ngOnInit(): void {
    const doctorId = this.route.snapshot.paramMap.get('id');
    if (doctorId) {
      this.loadDoctorDetails(doctorId);
      this.loadDoctorAvailability(doctorId);
    }
  }

  loadDoctorDetails(doctorId: string): void {
    this.doctorService.getDoctorById(doctorId).subscribe({
      next: (doctor) => {
        this.doctor = doctor;
      },
      error: (err) => console.error('Error loading doctor', err)
    });
  }

  loadDoctorAvailability(doctorId: string): void {
    this.disponibiliteService.getByDoctor(doctorId).subscribe({
      next: (disponibilites) => {
        this.events = this.mapDisponibilitesToEvents(disponibilites);
      },
      error: (err) => console.error('Error loading availability', err)
    });
  }

  private mapDisponibilitesToEvents(disponibilites: any[]): CalendarEvent[] {
    return disponibilites.map(dispo => ({
      start: this.parseDisponibiliteDate(dispo.jour, dispo.heureDebut),
      end: this.parseDisponibiliteDate(dispo.jour, dispo.heureFin),
      title: 'Available',
      color: { primary: '#1e90ff', secondary: '#D1E8FF' }
    }));
  }

  private parseDisponibiliteDate(dayOfWeek: string, time: string): Date {
    const date = new Date();
    const dayIndex = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']
      .indexOf(dayOfWeek.toUpperCase());
    
    date.setDate(date.getDate() + ((dayIndex - date.getDay() + 7) % 7));
    
    const [hours, minutes] = time.split(':').map(Number);
    return setMinutes(setHours(date, hours), minutes);
  }

  handleDayClick(event: any): void {
    // For day/week views
    if (event.date) {
      this.view = CalendarView.Day;
      this.viewDate = event.date;
    } 
    // For month view
    else if (event.day?.date) {
      this.view = CalendarView.Day;
      this.viewDate = event.day.date;
    }
  }
  
  handleEventClick(event: any): void {
    const clickedEvent = event.event || event;
    console.log('Event clicked', clickedEvent);
    
    this.dialog.open(AppointmentDialogComponent, {
      width: '500px',
      data: {
        doctor: this.doctor,
        availability: clickedEvent,
        startTime: clickedEvent.start,
        endTime: clickedEvent.end
      }
    });
  }
  onAddAvailability(doctorId: string) {
    console.log('doctorId',doctorId),
    this.router.navigate(['user/userProfile/doctor/', doctorId, 'AddAvailability']);
  }
}
