// src/app/features/patient-bills/patient-bill-list/patient-bill-list.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PatientBill } from 'src/app/models/patient-bill.model';
import { PatientBillService } from 'src/app/services/patient-bill.service';
//import { PatientBill } from '../../../core/models/patient-bill.model';
//import { PatientBillService } from '../../../core/services/patient-bill.service';

@Component({
  selector: 'app-patient-bill-list',
  templateUrl: './patient-bill-list.component.html',
  styleUrls: ['./patient-bill-list.component.scss']
})
export class PatientBillListComponent implements OnInit {
  patientBills: PatientBill[] = [];
  loading = false;
  error = '';

  constructor(
    private patientBillService: PatientBillService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getPatientBills();
  }

  getPatientBills(): void {
    this.loading = true;
    this.patientBillService.getPatientBills()
      .subscribe({
        next: (bills) => {
          this.patientBills = bills;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load patient bills';
          this.loading = false;
          console.error(err);
        }
      });
  }

  viewBillDetails(id: number): void {
    this.router.navigate(['/patient-bills', id]);
  }

  editBill(id: number): void {
    this.router.navigate(['/patient-bills/edit', id]);
  }

  createNewBill(): void {
    this.router.navigate(['/patient-bills/new']);
  }

  deleteBill(id: number): void {
    if (confirm('Are you sure you want to delete this bill?')) {
      this.patientBillService.deletePatientBill(id)
        .subscribe({
          next: () => {
            this.patientBills = this.patientBills.filter(bill => bill.id !== id);
          },
          error: (err) => {
            this.error = 'Failed to delete patient bill';
            console.error(err);
          }
        });
    }
  }

  getBillStatusClass(status: string): string {
    switch (status) {
      case 'PAID':
        return 'status-paid';
      case 'OVERDUE':
        return 'status-overdue';
      case 'PARTIALLY_PAID':
        return 'status-partial';
      default:
        return 'status-pending';
    }
  }
}