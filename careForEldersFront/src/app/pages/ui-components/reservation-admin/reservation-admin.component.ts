import { Component, OnInit } from '@angular/core';
import { Reservation, ReservationService } from '../reservation.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reservation-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reservation-admin.component.html',
  styleUrl: './reservation-admin.component.scss'
})
export class ReservationAdminComponent implements OnInit {
  reservations: Reservation[] = [];
  isLoading = true;

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.reservationService.getAllReservations().subscribe({
      next: (data) => {
        this.reservations = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load reservations', err);
        this.isLoading = false;
      }
    });
  }
}