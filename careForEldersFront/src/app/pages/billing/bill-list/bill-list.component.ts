// src/app/components/bill-list/bill-list.component.ts
import { Component, OnInit } from '@angular/core';
import { BillService } from 'src/app/services/PayBill/bill.service';
import { AuthService } from 'src/app/services/auth.service';
import { Bill } from 'src/app/entities/bill';

@Component({
  selector: 'app-bill-list',
  templateUrl: './bill-list.component.html',
  styleUrls: ['./bill-list.component.css']
})
export class BillListComponent implements OnInit {
  bills: Bill[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(
    private billService: BillService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadPatientBills();
  }

  loadPatientBills() {
    // Assuming your auth service can provide the current patient ID
    const patientId = this.authService.getCurrentPatientId();
    
    if (patientId) {
      this.billService.getBillsByPatient(patientId).subscribe({
        next: (bills) => {
          this.bills = bills;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading bills:', err);
          this.error = 'Failed to load bills. Please try again later.';
          this.isLoading = false;
        }
      });
    } else {
      this.error = 'Patient information not available.';
      this.isLoading = false;
    }
  }
}