import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-appointment-dialog',
  template: ` `,
  providers: [DatePipe]
})
export class AppointmentDialogComponent {
  patientName = '';
  notes = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private datePipe: DatePipe
  ) {}

  onCancel(): void {
    // Close without saving
  }

  onBook(): void {
    // Here you would call your appointment service to book the appointment
    console.log('Booking appointment:', {
      doctorId: this.data.doctor.id,
      startTime: this.data.startTime,
      endTime: this.data.endTime,
      patientName: this.patientName,
      notes: this.notes
    });
    
    // Close the dialog
  }
}