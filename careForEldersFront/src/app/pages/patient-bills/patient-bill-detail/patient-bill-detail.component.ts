// src/app/features/patient-bills/patient-bill-detail/patient-bill-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientBill } from 'src/app/models/patient-bill.model';
import { PatientBillService } from 'src/app/services/patient-bill.service';
//import { PatientBill } from '../../../core/models/patient-bill.model';
//import { PatientBillService } from '../../../core/services/patient-bill.service';

@Component({
  selector: 'app-patient-bill-detail',
  templateUrl: './patient-bill-detail.component.html',
  styleUrls: ['./patient-bill-detail.component.scss']
})
export class PatientBillDetailComponent implements OnInit {
  patientBill: PatientBill | null = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientBillService: PatientBillService
  ) {}

  ngOnInit(): void {
    this.getBillDetails();
  }

  getBillDetails(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loading = true;
      this.patientBillService.getPatientBill(id)
        .subscribe({
          next: (bill) => {
            this.patientBill = bill;
            this.loading = false;
          },
          error: (err) => {
            this.error = 'Failed to load bill details';
            this.loading = false;
            console.error(err);
          }
        });
    }
  }

  editBill(): void {
    if (this.patientBill) {
      this.router.navigate(['/patient-bills/edit', this.patientBill.id]);
    }
  }

  goBack(): void {
    this.router.navigate(['/patient-bills']);
  }

  getStatusClass(): string {
    if (!this.patientBill) return '';
    
    switch (this.patientBill.status) {
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

  calculateBalance(): number {
    if (!this.patientBill) return 0;
    return this.patientBill.totalAmount - this.patientBill.paidAmount;
  }
}