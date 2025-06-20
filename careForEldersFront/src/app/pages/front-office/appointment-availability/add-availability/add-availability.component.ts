import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-add-availability',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-availability.component.html',
  styleUrl: './add-availability.component.scss'
})
export class AddAvailabilityComponent {
  constructor(private http: HttpClient, private route: ActivatedRoute) {}
  
  
  availability = {
    doctorId: '', 
    jour: '',
    heureDebut: '',
    heureFin: ''
  };
  
  ngOnInit() {
  this.route.paramMap.subscribe(params => {
    const doctorIdFromParams = params.get('id'); // assuming your route is like /doctor/:id/add-availability
    if (doctorIdFromParams) {
      this.availability.doctorId = doctorIdFromParams;
    }
  });
}
  
  days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
  
  submitAvailability() {
    console.log('Submitting availability:', this.availability);
    this.http.post('http://localhost:8083/api/disponibilites', this.availability)
      .subscribe({
        next: res => {
          console.log('Added successfully');
        },
        error: err => {
          console.error('Error', err);
        }
      });
  }
}
