import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-add-availability',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './add-availability.component.html',
  styleUrl: './add-availability.component.scss'
})
export class AddAvailabilityComponent {
  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {}
  
  availability = {
    doctorId: '', 
    date: '',
    heureDebut: '',
    heureFin: '',
    slotDuration: 30
  };

  // No need for 'days' anymore
  selectedDate: Date | null = null;
  slotDurations = [15, 30, 45, 60];
  
  ngOnInit() {
    this.getUserInfo();
  }
  
  getUserInfo(): void {
    const userId = localStorage.getItem('user_id');
    if (!userId) {
      console.warn('User ID not found in local storage.');
      return;
    }
    this.availability.doctorId = userId;
  }
  
  submitAvailability() {
    if (!this.validateForm()) {
      return;
    }

    const payload = {
      ...this.availability,
      date: this.selectedDate ? this.selectedDate.toISOString().split('T')[0] : ''
    };

    this.http.post('http://localhost:8083/api/disponibilites', payload)
      .subscribe({
        next: (res) => {
          console.log('Done');
          this.router.navigate(['/user/userProfile']); // Redirect after success
        },
        error: (err) => {
          console.error('Error adding availability:', err);
        }
      });
  }

  private validateForm(): boolean {
    if (!this.availability.doctorId) {
      console.error('Doctor ID is missing');
      return false;
    }
    if (!this.selectedDate) {
      console.error('Date is not selected');
      return false;
    }
    if (!this.availability.heureDebut || !this.availability.heureFin) {
      console.error('Start time or end time is missing');
      return false;
    }
    return true;
  }
}