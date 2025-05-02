// src/app/billing/components/bill-create/bill-create.component.ts
import { Component } from '@angular/core';
import { BillService } from '../services/bill.service';
import { Bill, BillStatus } from '../models/bill';
import { ServiceItem } from '../models/service-item';

@Component({
  selector: 'app-bill-create',
  templateUrl: './bill-create.component.html'
})
export class BillCreateComponent {
  newBill: Bill = {
    patientId: 0,
    patientName: '',
    billDate: new Date().toISOString().split('T')[0],
    services: [],
    status: 'DRAFT' as BillStatus,
    totalAmount: 0,
    id: 0
  };

  newService: ServiceItem = {
    serviceName: '',
    description: '',
    rate: 0,
    quantity: 1
  };

  constructor(private billService: BillService) {}

  addService(): void {
    this.newBill.services.push({...this.newService});
    this.newService = { serviceName: '', description: '', rate: 0, quantity: 1 };
    this.calculateTotal();
  }

  calculateTotal(): void {
    this.newBill.totalAmount = this.newBill.services.reduce(
      (total, item) => total + (item.rate * item.quantity), 0
    );
  }

  onSubmit(): void {
    this.billService.create(this.newBill).subscribe({
      next: (createdBill) => {
        console.log('Bill created:', createdBill);
        // Reset form or navigate
      },
      error: (err) => console.error('Error creating bill:', err)
    });
  }
}