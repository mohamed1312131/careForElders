// src/app/core/models/patient-bill.model.ts
export interface PatientBill {
    id?: number;
    patientId: number;
    patientName: string;
    billNumber: string;
    billDate: Date;
    dueDate: Date;
    totalAmount: number;
    paidAmount: number;
    status: 'PENDING' | 'PAID' | 'OVERDUE' | 'PARTIALLY_PAID';
    items: BillItem[];
    notes?: string;
  }
  
  export interface BillItem {
    id?: number;
    description: string;
    quantity: number;
    unitPrice: number;
    amount: number;
    serviceDate?: Date;
    serviceType: string;
  }