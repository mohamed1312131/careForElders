import { Component } from '@angular/core';
import { AppointementService } from '../service/appointement-service/appointement.service';
import { User } from '../User';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search-doctor',
  templateUrl: './search-doctor.component.html',
  styleUrl: './search-doctor.component.scss'
})
export class SearchDoctorComponent {

  users: User[] = [];

  constructor(private userService: AppointementService, private router: Router) {}

  ngOnInit(): void {
    this.userService.getAllDoctors().subscribe({
      next: (data) => {
        this.users = data;
      },
      error: (err) => {
        console.error('Error loading users:', err);
      }
    });

    
  }

  viewDoctorDetails(doctorId: string) {
    console.log('DoctorDetailsComponent loaded');
    this.router.navigate(['/user/userProfile/doctor', doctorId]);

    console.log('DoctorDetailsComponent loaded',doctorId);


  }
}