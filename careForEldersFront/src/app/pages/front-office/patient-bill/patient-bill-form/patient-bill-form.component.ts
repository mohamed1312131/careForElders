// src/app/features/patient-bills/patient-bill-form/patient-bill-form.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BillItem, PatientBill } from 'src/app/models/patient-bill.model';
import { PatientBillService } from 'src/app/services/patient-bill.service';
//import { PatientBill, BillItem } from '../../../core/models/patient-bill.model';
//import { PatientBillService } from '../../../core/services/patient-bill.service';

@Component({
  selector: 'app-patient-bill-form',
  templateUrl: './patient-bill-form.component.html',
  styleUrls: ['./patient-bill-form.component.scss']
})
export class PatientBillFormComponent implements OnInit {
  billForm: FormGroup;
  isEditMode = false;
  billId: number | null = null;
  loading = false;
  submitting = false;
  error = '';
  
  serviceTypes = [
    'CONSULTATION',
    'LABORATORY',
    'RADIOLOGY',
    'MEDICATION',
    'SURGERY',
    'THERAPY',
    'OTHER'
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private patientBillService: PatientBillService
  ) {
    this.billForm = this.createBillForm();
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.billId = +id;
      this.loadBillData(this.billId);
    }
  }

  createBillForm(): FormGroup {
    return this.fb.group({
      patientId: ['', [Validators.required]],
      patientName: ['', [Validators.required]],
      billNumber: ['', [Validators.required]],
      billDate: [new Date(), [Validators.required]],
      dueDate: [new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), [Validators.required]],
      status: ['PENDING', [Validators.required]],
      totalAmount: [{ value: 0, disabled: true }],
      paidAmount: [0, [Validators.required, Validators.min(0)]],
      notes: [''],
      items: this.fb.array([this.createBillItemForm()])
    });
  }

  createBillItemForm(item?: BillItem): FormGroup {
    return this.fb.group({
      description: [item?.description || '', [Validators.required]],
      quantity: [item?.quantity || 1, [Validators.required, Validators.min(1)]],
      unitPrice: [item?.unitPrice || 0, [Validators.required, Validators.min(0)]],
      amount: [{ value: item?.amount || 0, disabled: true }],
      serviceDate: [item?.serviceDate || new Date()],
      serviceType: [item?.serviceType || 'CONSULTATION', [Validators.required]]
    });
  }

  get items(): FormArray {
    return this.billForm.get('items') as FormArray;
  }

  addItem(): void {
    this.items.push(this.createBillItemForm());
  }

  removeItem(index: number): void {
    if (this.items.length > 1) {
      this.items.removeAt(index);
      this.calculateTotals();
    }
  }

  loadBillData(id: number): void {
    this.loading = true;
    this.patientBillService.getPatientBill(id)
      .subscribe({
        next: (bill) => {
          // Clear existing items and add new ones
          while (this.items.length) {
            this.items.removeAt(0);
          }
          
          bill.items.forEach(item => {
            this.items.push(this.createBillItemForm(item));
          });
          
          this.billForm.patchValue({
            patientId: bill.patientId,
            patientName: bill.patientName,
            billNumber: bill.billNumber,
            billDate: bill.billDate,
            dueDate: bill.dueDate,
            status: bill.status,
            paidAmount: bill.paidAmount,
            notes: bill.notes || ''
          });
          
          this.calculateTotals();
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load bill data';
          this.loading = false;
          console.error(err);
        }
      });
  }

  calculateItemAmount(index: number): void {
    const itemGroup = this.items.at(index) as FormGroup;
    const quantity = itemGroup.get('quantity')?.value || 0;
    const unitPrice = itemGroup.get('unitPrice')?.value || 0;
    const amount = quantity * unitPrice;
    
    itemGroup.get('amount')?.setValue(amount, { emitEvent: false });
    this.calculateTotals();
  }

  calculateTotals(): void {
    let total = 0;
    
    for (let i = 0; i < this.items.length; i++) {
      const itemGroup = this.items.at(i) as FormGroup;
      const quantity = itemGroup.get('quantity')?.value || 0;
      const unitPrice = itemGroup.get('unitPrice')?.value || 0;
      total += quantity * unitPrice;
    }
    
    this.billForm.get('totalAmount')?.setValue(total);
    this.updateBillStatus();
  }

  updateBillStatus(): void {
    const totalAmount = this.billForm.get('totalAmount')?.value || 0;
    const paidAmount = this.billForm.get('paidAmount')?.value || 0;
    
    let status = 'PENDING';
    
    if (paidAmount >= totalAmount) {
      status = 'PAID';
    } else if (paidAmount > 0) {
      status = 'PARTIALLY_PAID';
    }
    
    const dueDate = new Date(this.billForm.get('dueDate')?.value);
    if (status !== 'PAID' && dueDate < new Date()) {
      status = 'OVERDUE';
    }
    
    this.billForm.get('status')?.setValue(status);
  }

  onSubmit(): void {
    if (this.billForm.invalid) {
      // Mark all fields as touched to trigger validation messages
      this.markFormGroupTouched(this.billForm);
      return;
      
    }
    
    this.submitting = true;
    
    // Enable disabled controls to include in form value
    this.billForm.get('totalAmount')?.enable();
    for (let i = 0; i < this.items.length; i++) {
      (this.items.at(i) as FormGroup).get('amount')?.enable();
    }
    
    const formValue = this.billForm.value;
    
    // Create bill object
    const bill: PatientBill = {
      ...formValue,
      id: this.billId || undefined
    };
    
    // Save bill
    const saveObservable = this.isEditMode
      ? this.patientBillService.updatePatientBill(bill)
      : this.patientBillService.createPatientBill(bill);
    
    saveObservable.subscribe({
      next: () => {
        this.router.navigate(['/patient-bills']);
      },
      error: (err) => {
        this.error = 'Failed to save bill';
        this.submitting = false;
        console.error(err);
        
        // Disable controls again
        this.billForm.get('totalAmount')?.disable();
        for (let i = 0; i < this.items.length; i++) {
          (this.items.at(i) as FormGroup).get('amount')?.disable();
        }
      }
    });
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      } else if (control instanceof FormArray) {
        for (let i = 0; i < control.length; i++) {
          if (control.at(i) instanceof FormGroup) {
            this.markFormGroupTouched(control.at(i) as FormGroup);
          }
        }
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/patient-bills']);
  }
}