import { HttpClient } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-appointment-dialog',
  template: `
    <mat-card class="appointment-card">
      <mat-card-header>
        <div mat-card-avatar class="doctor-avatar">
          {{ data.doctor.firstName.charAt(0) }}{{ data.doctor.lastName.charAt(0) }}
        </div>
        <mat-card-title>Book Appointment</mat-card-title>
        <mat-card-subtitle>
          With Dr. {{ data.doctor.firstName }} {{ data.doctor.lastName }}
        </mat-card-subtitle>
      </mat-card-header>

      <mat-divider></mat-divider>

      <mat-card-content>
        <div class="appointment-details">
          <div class="detail-row">
            <span class="detail-label">Date:</span>
            <span class="detail-value">{{ data.startTime | date:'fullDate' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Time:</span>
            <span class="detail-value">
              {{ data.startTime | date:'shortTime' }} - {{ data.endTime | date:'shortTime' }}
            </span>
          </div>
          <div class="detail-row" *ngIf="patientName">
            <span class="detail-label">Patient:</span>
            <span class="detail-value">{{ patientName }}</span>
          </div>
          <mat-form-field appearance="outline" class="w-full" *ngIf="!patientName">
            <mat-label>Patient Name</mat-label>
            <input matInput [(ngModel)]="patientName" required>
          </mat-form-field>
        </div>
        
        <mat-form-field appearance="outline" class="w-full">
          <mat-label>Notes (Optional)</mat-label>
          <textarea matInput [(ngModel)]="notes" rows="3"></textarea>
        </mat-form-field>
      </mat-card-content>

      <mat-divider></mat-divider>

      <mat-card-actions align="end">
        <button mat-button class="cancel-button" (click)="onCancel()">Cancel</button>
        <button mat-raised-button 
                color="primary" 
                class="confirm-button"
                [disabled]="isLoading"
                (click)="onBook()">
          {{ isLoading ? 'Booking...' : 'Confirm Appointment' }}
        </button>
      </mat-card-actions>
    </mat-card>
  `,
  styles: [`
    .appointment-card {
      border-radius: 12px;
      padding: 20px;
      width: 100%;
      max-width: 500px;
      margin: 0 auto;
      font-family: 'Roboto', sans-serif;
    }

    .doctor-avatar {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background-color: #673ab7;
      color: white;
      font-weight: bold;
      font-size: 16px;
    }

    .appointment-details {
      padding: 16px 0;
    }

    .detail-row {
      display: flex;
      margin-bottom: 12px;
    }

    .detail-label {
      font-weight: 500;
      width: 100px;
      color: #555;
    }

    .detail-value {
      flex: 1;
      padding: 8px 0;
    }

    .w-full {
      width: 100%;
      margin-top: 16px;
    }

    .cancel-button {
      margin-right: 8px;
      color: #f44336;
    }

    .confirm-button {
      background-color: #673ab7;
      color: white;
    }

    mat-divider {
      margin: 16px 0;
    }

    mat-form-field {
      margin-bottom: 8px;
    }

    mat-card-title {
      font-size: 1.25rem;
      font-weight: 500;
    }

    mat-card-subtitle {
      color: rgba(0, 0, 0, 0.87);
    }

    .error-snackbar {
      background-color: #f44336;
      color: white;
    }`]
})
export class AppointmentDialogComponent implements OnInit {
  patientName: string = '';
  notes: string = '';
  isLoading: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<AppointmentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.getUserFromLocalStorage();
  }

  private getUserFromLocalStorage(): void {
    try {
      const storageKeys = ['currentUser', 'user', 'authUser', 'loggedInUser'];
      for (const key of storageKeys) {
        const item = localStorage.getItem(key);
        if (item) {
          const user = JSON.parse(item);
          const name = user.name || user.fullName || `${user.firstName || ''} ${user.lastName || ''}`.trim();
          if (name) this.patientName = name;
          break;
        }
      }
    } catch (err) {
      console.error('Failed to load user from storage', err);
    }
  }

  private getUserIdFromLocalStorage(): string {
    try {
      const storageKeys = ['currentUser', 'user', 'authUser', 'loggedInUser'];
      for (const key of storageKeys) {
        const item = localStorage.getItem(key);
        if (item) {
          const user = JSON.parse(item);
          return user.id || user._id || user.userId || '';
        }
      }
    } catch (err) {
      console.error('Failed to get user ID', err);
    }
    return '';
  }

  private formatDate(date: Date): string {
    return new Date(date).toISOString().split('T')[0]; // YYYY-MM-DD
  }

  private formatTime(date: Date): string {
  const d = new Date(date);
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  return `${hours}:${minutes}:00`; // LocalTime expects HH:mm:ss
}

  onCancel(): void {
    this.dialogRef.close();
  }





 onBook(): void {
  const userId = this.getUserIdFromLocalStorage(); // Ensure userId is assigned first
  if (!userId || !this.patientName) {
    this.snackBar.open('User information is missing. Please log in again.', 'Close', {
      duration: 3000,
      panelClass: ['error-snackbar']
    });
    return;
  }

  // Create the reservation data with proper formatting
  let reservationData = {
    date: this.formatDate(this.data.startTime),
    heure: this.formatTime(this.data.startTime),
    statut: 'PLANIFIEE',
    userId: userId,  // Correctly passing the userId
    soignantId: this.data.doctor.id,
    notes: this.notes || ''
  };

  this.isLoading = true;

  this.http.post('http://localhost:8083/reservations', reservationData)
    .subscribe({
      next: (res) => {
        this.isLoading = false;
        this.snackBar.open('Appointment booked successfully!', 'Close', { duration: 3000 });
        this.dialogRef.close({ confirmed: true, reservation: res });
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Booking error:', err);
        this.snackBar.open('Booking failed. Please try again.', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });

  console.log('Sending reservation:', reservationData);
}
}