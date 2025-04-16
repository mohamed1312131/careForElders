// src/app/pages/billing/invoice/invoice.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BillService } from 'src/app/services/PayBill/bill.service';
import { DatePipe } from '@angular/common';
import { Invoice, Patient } from 'src/app/entities/invoice';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.css'],
  providers: [DatePipe]
})
export class InvoiceComponent implements OnInit {
  invoice: Invoice | null = null;
  patientDetails: Patient | null = null;
  isLoading = true;
  isSending = false;
  invoiceId: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private billService: BillService,
    private datePipe: DatePipe,
    private snackBar: MatSnackBar
  ) {
    this.invoiceId = +(this.route.snapshot.paramMap.get('id') || 0);
  }

  ngOnInit(): void {
    this.loadInvoice();
  }

  loadInvoice() {
    this.isLoading = true;
    this.billService.getInvoice(this.invoiceId).subscribe({
      next: (invoice) => {
        this.invoice = invoice;
        // Load patient details separately if needed
        this.loadPatientDetails(invoice.patientId);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load invoice', err);
        this.showError('Failed to load invoice');
        this.isLoading = false;
        this.router.navigate(['/billing/bills']);
      }
    });
  }

  loadPatientDetails(patientId: number) {
    // Implement patient details loading if needed
    // this.patientService.getPatient(patientId).subscribe(...)
    // For now, we'll just mock it
    this.patientDetails = {
      id: patientId,
      name: 'Patient Name',
      email: 'patient@example.com',
      phone: '(123) 456-7890',
      address: '123 Main St, City'
    };
  }

  sendPdfInvoice() {
    if (!this.invoice || !this.patientDetails) return;
    
    this.isSending = true;
    const patientEmail = this.patientDetails.email;
    
    this.billService.sendInvoiceByEmail(this.invoiceId, patientEmail).subscribe({
      next: () => {
        this.showSuccess('PDF invoice sent successfully');
        this.isSending = false;
      },
      error: (err) => {
        console.error('Failed to send PDF invoice', err);
        this.showError('Failed to send PDF invoice');
        this.isSending = false;
      }
    });
  }

  private showSuccess(message: string) {
    this.snackBar.open(message, 'Close', { 
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string) {
    this.snackBar.open(message, 'Close', { 
      duration: 3000,
      panelClass: ['error-snackbar'] 
    });
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return '';
    return this.datePipe.transform(date, 'mediumDate') || '';
  }
}