import { HttpClient } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { formatDate } from '@angular/common';

@Component({
  selector: 'app-appointment-dialog',
  templateUrl: './appointment-dialog.component.html',
  styleUrls: ['./appointment-dialog.component.scss']
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

    if (appointmentType === 'PRESENTIEL' && !this.location) {
      this.snackBar.open('Please enter the appointment location.', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      this.loading = false;
      return;
    }

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