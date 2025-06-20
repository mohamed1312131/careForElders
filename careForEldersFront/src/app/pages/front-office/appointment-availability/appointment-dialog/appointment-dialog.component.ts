import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-appointment-dialog',
  template: `
    <h2 mat-dialog-title>Book Appointment</h2>
    <mat-dialog-content>
      <p>With Dr. {{data.doctor?.firstName}} {{data.doctor?.lastName}}</p>
      <p>On {{data.startTime | date:'fullDate'}}</p>
      <p>From {{data.startTime | date:'shortTime'}} to {{data.endTime | date:'shortTime'}}</p>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button (click)="dialogRef.close()">Cancel</button>
      <button mat-button color="primary" (click)="dialogRef.close('confirm')">Confirm</button>
    </mat-dialog-actions>
  `,
  styles: [`
    :host {
      display: block;
      background: white;
      padding: 24px;
      border-radius: 4px;
      box-shadow: 0 11px 15px -7px rgba(0,0,0,.2);
    }
  `]
})
export class AppointmentDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<AppointmentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}
}