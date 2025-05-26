import { HttpClient } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { formatDate } from '@angular/common';

@Component({
  selector: 'app-appointment-dialog',
  template: `
    <mat-card class="appointment-card">
      <mat-card-header>
        <div mat-card-avatar class="doctor-avatar">
          {{ getDoctorInitials(data.doctorInfo.name) }}
        </div>
        <mat-card-title>Book Appointment</mat-card-title>
        <mat-card-subtitle>
          With Dr. {{ data.doctorInfo.name }}
          <span *ngIf="data.doctorInfo.specialty">({{ data.doctorInfo.specialty }})</span>
        </mat-card-subtitle>
      </mat-card-header>

      <mat-divider></mat-divider>

      <mat-card-content>
        <div class="appointment-details">
          <div class="detail-row">
            <span class="detail-label">Date:</span>
            <span class="detail-value">{{ formattedDate }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Time:</span>
            <span class="detail-value">{{ formattedTime }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">Duration:</span>
            <span class="detail-value">{{ data.availability.slotDuration }} minutes</span>
          </div>
          <div class="detail-row" *ngIf="patientName">
            <span class="detail-label">Patient:</span>
            <span class="detail-value">{{ patientName }}</span>
          </div>
          <mat-form-field appearance="outline" class="w-full" *ngIf="!patientName">
            <mat-label>Patient Name</mat-label>
            <input matInput [(ngModel)]="patientName" required>
          </mat-form-field>
          <!-- Show location only if appointmentType is PRESENTIEL -->
          <mat-form-field appearance="outline" class="w-full" *ngIf="pendingType === 'PRESENTIEL'">
            <mat-label>Lieu du rendez-vous</mat-label>
            <input matInput [(ngModel)]="location" required>
          </mat-form-field>
        </div>
        
        <mat-form-field appearance="outline" class="w-full">
          <mat-label>Notes (Optional)</mat-label>
          <textarea matInput [(ngModel)]="notes" rows="3"></textarea>
        </mat-form-field>
      </mat-card-content>

      <mat-divider></mat-divider>

      <mat-card-actions align="end">
        <button mat-raised-button 
                color="primary" 
                class="presentiel-button"
                [disabled]="isLoading || !patientName"
                (click)="bookAppointment('PRESENTIEL')">
          Présentiel
        </button>
        <button mat-raised-button 
                color="accent" 
                class="enligne-button"
                [disabled]="isLoading || !patientName"
                (click)="bookAppointment('EN_LIGNE')">
          En ligne
        </button>
      </mat-card-actions>
    </mat-card>
    <div *ngIf="loading" class="loading-overlay">
      <mat-spinner></mat-spinner>
      <!-- Or use any other spinner you like -->
    </div>
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

    .presentiel-button {
      background-color: #673ab7;
      color: white;
      margin-right: 8px;
    }

    .enligne-button {
      background-color: #ff4081;
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
    }
    .loading-overlay {
      position: fixed;
      top: 0; left: 0; right: 0; bottom: 0;
      background: rgba(255,255,255,0.6);
      z-index: 1000;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  `]
})

export class AppointmentDialogComponent implements OnInit {
  patientName: string = '';
  notes: string = '';
  isLoading: boolean = false;
  formattedDate: string = '';
  formattedTime: string = '';
  loading = false;
  pendingType: 'EN_LIGNE' | 'PRESENTIEL' | null = null;
  location: string = '';

  constructor(
    public dialogRef: MatDialogRef<AppointmentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.formatDisplayTimes();
    this.getUserFromLocalStorage();
  }

  getDoctorInitials(name: string): string {
    return name.split(' ').map(n => n.charAt(0)).join('');
  }

  private formatDisplayTimes(): void {
    this.formattedDate = formatDate(this.data.slot.start, 'fullDate', 'en-US');
    const startTime = formatDate(this.data.slot.start, 'h:mm a', 'en-US');
    const endTime = formatDate(this.data.slot.end, 'h:mm a', 'en-US');
    this.formattedTime = `${startTime} - ${endTime}`;
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

  private formatDateForBackend(date: Date): string {
    return formatDate(date, 'yyyy-MM-dd', 'en-US');
  }

  private formatTimeForBackend(date: Date): string {
    return formatDate(date, 'HH:mm:ss', 'en-US');
  }

  private generateMeetingLink(userId: string, doctorId: string): string {
    const userPart = userId.slice(-4);
    const doctorPart = doctorId.slice(-4);
    const randomString = Math.random().toString(36).substring(2, 6);
    return `https://meet.jit.si/med-${userPart}-${doctorPart}-${randomString}`;
  }

  bookAppointment(appointmentType: 'EN_LIGNE' | 'PRESENTIEL'): void {
    this.pendingType = appointmentType;
    console.log('Setting loading to true...');
    this.loading = true;
    const userId = this.getUserIdFromLocalStorage();
    if (!userId || !this.patientName) {
      this.snackBar.open('User information is missing. Please log in again.', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      this.loading = false;
      return;
    }

    // Require location for PRESENTIEL
    if (appointmentType === 'PRESENTIEL' && !this.location) {
      this.snackBar.open('Veuillez saisir le lieu du rendez-vous.', 'Fermer', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      this.loading = false;
      return;
    }

    // Use flexible object type so we can add meetingLink/location dynamically
    const reservationData: Record<string, any> = {
      date: this.formatDateForBackend(this.data.slot.start),
      heure: this.formatTimeForBackend(this.data.slot.start),
      endTime: this.formatTimeForBackend(this.data.slot.end),
      statut: 'CONFIRME',
      userId: userId,
      doctorId: this.data.doctorInfo.id,
      reservationType: appointmentType,
      notes: this.notes || '',
      duration: this.data.availability.slotDuration,
    };

    if (appointmentType === 'EN_LIGNE') {
      reservationData['meetingLink'] = this.generateMeetingLink(userId, this.data.doctorInfo.id);
    }
    if (appointmentType === 'PRESENTIEL') {
      reservationData['location'] = this.location;
    }

    this.isLoading = true;

    this.http.post('http://localhost:8083/api/reservations', reservationData)
      .subscribe({
        next: (res: any) => {
          this.isLoading = false;
          this.loading = false;

          let message = 'Appointment booked successfully!';
          if (appointmentType === 'EN_LIGNE' && res.meetingLink) {
            message = 'Online appointment booked! Meeting link: ' + res.meetingLink;
          }

          this.snackBar.open(message, 'Close', {
            duration: 5000,
            panelClass: ['success-snackbar']
          });

          this.dialogRef.close(true);
          this.dialogRef.close('confirmed'); // This triggers the reload
        },
        error: (err) => {
          this.isLoading = false;
          this.loading = false;
          this.snackBar.open('Failed to book appointment. Please try again.', 'Close', {
            duration: 4000,
            panelClass: ['error-snackbar']
          });
          console.error('Error booking appointment:', err);
        }
      });
  }
}