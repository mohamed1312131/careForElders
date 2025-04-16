// src/app/pages/billing/bill-create/bill-create.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormArray, FormGroup, Validators } from '@angular/forms';
import { BillService } from 'src/app/services/PayBill/bill.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-bill-create',
  templateUrl: './bill-create.component.html',
  styleUrls: ['./bill-create.component.css']
})
export class BillCreateComponent implements OnInit {
  billForm: FormGroup;
  billTypes: string[] = [];
  paymentMethods: string[] = [];
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private billService: BillService,
    private router: Router
  ) {
    this.billForm = this.fb.group({
      patientId: ['', Validators.required],
      type: ['', Validators.required],
      dueDate: ['', Validators.required],
      paymentMethod: [''],
      notes: [''],
      items: this.fb.array([this.createBillItem()])
    });
  }

  ngOnInit(): void {
    this.loadBillTypes();
    this.loadPaymentMethods();
  }

  createBillItem(): FormGroup {
    return this.fb.group({
      description: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitPrice: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  get items(): FormArray {
    return this.billForm.get('items') as FormArray;
  }

  addItem(): void {
    this.items.push(this.createBillItem());
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
  }

  loadBillTypes(): void {
    this.billService.getBillTypes().subscribe(types => {
      this.billTypes = types;
    });
  }

  loadPaymentMethods(): void {
    this.billService.getPaymentMethods().subscribe(methods => {
      this.paymentMethods = methods;
    });
  }

  calculateTotal(): number {
    return this.items.controls.reduce((total, item) => {
      return total + (item.value.quantity * item.value.unitPrice);
    }, 0);
  }

  onSubmit(): void {
    if (this.billForm.invalid || this.isSubmitting) return;

    this.isSubmitting = true;
    const formValue = this.billForm.value;
    const billData = {
      patientId: formValue.patientId,
      type: formValue.type,
      dueDate: formValue.dueDate,
      paymentMethod: formValue.paymentMethod,
      notes: formValue.notes,
      items: formValue.items,
      amount: this.calculateTotal()
    };

    this.billService.createBill(billData).subscribe({
      next: (createdBill) => {
        this.router.navigate(['/billing/bills', createdBill.id]);
      },
      error: (err) => {
        console.error('Error creating bill:', err);
        this.isSubmitting = false;
      }
    });
  }
}