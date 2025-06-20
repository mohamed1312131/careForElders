import { Component, Inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { CalendarEvent } from 'angular-calendar';
import { JitsiDialogComponent } from '../jitsi-dialog/jitsi-dialog.component';

@Component({
  selector: 'app-event-details-dialog',
  templateUrl: './event-details-dialog.component.html',
  styleUrl: './event-details-dialog.component.scss'
})
export class EventDetailsDialogComponent {
   @Input() appointment: any;
  showGoToPayments: boolean = false;
  reservationMeta : any;

  constructor(
    public dialogRef: MatDialogRef<EventDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CalendarEvent & { fromMySchedule?: boolean },
    private dialog: MatDialog,
    private router: Router
  ) {
    
    this.showGoToPayments = !!data.fromMySchedule;
  }

  close(): void {
    this.dialogRef.close();
  }

  onUpdate(): void {

    console.log('Update clicked for:', this.data.meta);
    // Save the reservation meta to localStorage
    localStorage.setItem('reservationToUpdate', JSON.stringify(this.reservationMeta));
    // Redirect to the calendar view of the doctor, tell calendar to go into "update" mode
    this.router.navigate(['/user/userProfile/doctor/', this.reservationMeta.data.doctorId], {
      queryParams: { update: 'true' }
    });

  // Use data.meta instead of reservationMeta since that's where your data is stored
  const reservationData = this.data.meta?.data;
  
  if (!reservationData) {
    console.error('No reservation data available for update');
    return;

  }

  console.log('Update clicked for:', this.data.meta);
  
  // Save the reservation data to localStorage
  localStorage.setItem('reservationToUpdate', JSON.stringify(this.data.meta));
  
  // Verify doctorId exists before navigation
  if (reservationData.doctorId) {
    this.router.navigate(['/user/userProfile/doctor/', reservationData.doctorId], {
      queryParams: { update: 'true' }
    });
  } else {
    console.error('Doctor ID not found in reservation data');
  }
}

  goToPayments(): void {
    console.log('Go to Payments clicked for:', this.data.meta);
    // Navigate to payments or open another dialog
  }
startCall(meetingLink: string) {
  // Extract meeting ID from the full link
  const meetingId = meetingLink.split('/').pop();
  
  this.dialog.open(JitsiDialogComponent, {
    width: '90%',
    height: '90%',
    data: { roomName: meetingId }
  });
}
bookSlot(slot: any): void {
    console.log('Booking slot:', slot);
    // You can add more logic here (API call, feedback, etc.)
  }

isPastEvent(endDate: Date | undefined | string): boolean {
  if (!endDate) return true; // Treat missing date as past
  const date = new Date(endDate);
  return date < new Date();
}


}
