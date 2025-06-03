import { Component, OnInit } from '@angular/core';
import { ChartType } from 'ng-apexcharts';
import { ChartConfiguration, ChartData } from 'chart.js';

// Mock data interfaces
interface Reservation {
  id: string;
  userId: string;
  doctorId: string;
  date: string;
  heure: string;
  endTime: string;
  statut: 'CONFIRME' | 'ANNULE' | 'TERMINE';
  reservationType: 'EN_LIGNE' | 'PRESENTIEL';
  meetingLink?: string;
  location?: string;
}

interface Disponibilite {
  id: string;
  doctorId: string;
  date: string;
  heureDebut: string;
  heureFin: string;
  slotDuration: number;
}

interface UserDTO {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: 'PATIENT' | 'DOCTOR' | 'ADMIN';
}

@Component({
  selector: 'app-badge',
  templateUrl: './badge.component.html',
  styleUrl: './badge.component.Scss'
})
export class AppBadgeComponent implements OnInit {
  reservations: Reservation[] = [
    {
      id: 'res1',
      userId: 'user1',
      doctorId: 'doc1',
      date: '2023-06-15',
      heure: '09:00',
      endTime: '09:30',
      statut: 'CONFIRME',
      reservationType: 'EN_LIGNE',
      meetingLink: 'https://meet.example.com/abc123'
    },
    {
      id: 'res2',
      userId: 'user2',
      doctorId: 'doc2',
      date: '2023-06-15',
      heure: '10:00',
      endTime: '10:45',
      statut: 'TERMINE',
      reservationType: 'PRESENTIEL',
      location: 'Room 101'
    },
    {
      id: 'res3',
      userId: 'user3',
      doctorId: 'doc1',
      date: '2023-06-16',
      heure: '14:00',
      endTime: '14:30',
      statut: 'ANNULE',
      reservationType: 'EN_LIGNE',
      meetingLink: 'https://meet.example.com/xyz456'
    }
  ];

  doctorsAvailability: Disponibilite[] = [
    {
      id: 'avail1',
      doctorId: 'doc1',
      date: '2023-06-15',
      heureDebut: '09:00',
      heureFin: '17:00',
      slotDuration: 30
    },
    {
      id: 'avail2',
      doctorId: 'doc2',
      date: '2023-06-15',
      heureDebut: '08:00',
      heureFin: '12:00',
      slotDuration: 45
    }
  ];

  users: UserDTO[] = [
    { id: 'user1', firstName: 'John', lastName: 'Doe', email: 'john@example.com', role: 'PATIENT' },
    { id: 'user2', firstName: 'Jane', lastName: 'Smith', email: 'jane@example.com', role: 'PATIENT' },
    { id: 'user3', firstName: 'Bob', lastName: 'Johnson', email: 'bob@example.com', role: 'PATIENT' }
  ];

  doctors: UserDTO[] = [
    { id: 'doc1', firstName: 'Dr. Sarah', lastName: 'Williams', email: 'sarah@example.com', role: 'DOCTOR' },
    { id: 'doc2', firstName: 'Dr. Michael', lastName: 'Brown', email: 'michael@example.com', role: 'DOCTOR' }
  ];

  // Filter properties
  statusFilter: 'CONFIRME' | 'ANNULE' | 'TERMINE' | 'ALL' = 'ALL';
  typeFilter: 'EN_LIGNE' | 'PRESENTIEL' | 'ALL' = 'ALL';
  doctorFilter = 'ALL';

  // Analytics
  reservationStats = {
    total: 3,
    confirmed: 1,
    cancelled: 1,
    completed: 1
  };

  doctorUtilization = [
    { doctorName: 'Dr. Sarah Williams', utilization: 67 },
    { doctorName: 'Dr. Michael Brown', utilization: 33 }
  ];

  // Chart configurations
  public statusChartData: ChartData<'pie'> = {
    labels: ['Confirmed', 'Cancelled', 'Completed'],
    datasets: [{
      data: [1, 1, 1],
      backgroundColor: ['#36A2EB', '#FF6384', '#4BC0C0']
    }]
  };

  public typeChartData: ChartData<'doughnut'> = {
    labels: ['Online', 'In-Person'],
    datasets: [{
      data: [2, 1],
      backgroundColor: ['#FF6384', '#36A2EB']
    }]
  };

  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    scales: {
      x: {},
      y: {
        min: 0,
        max: 100
      }
    }
  };

  public barChartType: ChartType = 'bar';

  // Table columns
  displayedColumns: string[] = ['id', 'patient', 'doctor', 'date', 'time', 'type', 'status', 'actions'];

  ngOnInit(): void {
    // Initialization code can go here if needed
    this.updateStats();
  }

  // Helper methods
  get filteredReservations(): Reservation[] {
    return this.reservations.filter(reservation => {
      const matchesStatus = this.statusFilter === 'ALL' || reservation.statut === this.statusFilter;
      const matchesType = this.typeFilter === 'ALL' || reservation.reservationType === this.typeFilter;
      const matchesDoctor = this.doctorFilter === 'ALL' || reservation.doctorId === this.doctorFilter;
      
      return matchesStatus && matchesType && matchesDoctor;
    });
  }

  getDoctorName(doctorId: string): string {
    const doctor = this.doctors.find(d => d.id === doctorId);
    return doctor ? `${doctor.firstName} ${doctor.lastName}` : 'Unknown Doctor';
  }

  getUserName(userId: string): string {
    const user = this.users.find(u => u.id === userId);
    return user ? `${user.firstName} ${user.lastName}` : 'Unknown User';
  }

  // Mock actions
  cancelReservation(reservationId: string): void {
    const reservation = this.reservations.find(r => r.id === reservationId);
    if (reservation) {
      reservation.statut = 'ANNULE';
      this.updateStats();
    }
  }

  updateStats(): void {
    this.reservationStats = {
      total: this.reservations.length,
      confirmed: this.reservations.filter(r => r.statut === 'CONFIRME').length,
      cancelled: this.reservations.filter(r => r.statut === 'ANNULE').length,
      completed: this.reservations.filter(r => r.statut === 'TERMINE').length
    };
    
    this.statusChartData = {
      labels: ['Confirmed', 'Cancelled', 'Completed'],
      datasets: [{
        data: [
          this.reservationStats.confirmed,
          this.reservationStats.cancelled,
          this.reservationStats.completed
        ],
        backgroundColor: ['#36A2EB', '#FF6384', '#4BC0C0']
      }]
    };
  }

  deleteAvailability(availabilityId: string): void {
    this.doctorsAvailability = this.doctorsAvailability.filter(a => a.id !== availabilityId);
  }

  // For demo purposes
  now = new Date();
}