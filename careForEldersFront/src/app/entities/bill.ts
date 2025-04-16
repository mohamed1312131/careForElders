// src/app/entities/bill.ts
export interface Bill {
    id: number;
    patientId: number;
    doctorId: number;
    visitDate: Date;
    items: BillItem[];
    totalAmount: number;
    status: 'DRAFT' | 'ISSUED' | 'PAID' | 'CANCELLED';
    createdAt: Date;
    updatedAt: Date;
  }
  
  export interface BillItem {
    serviceId: number;
    serviceCode: string;
    serviceName: string;
    quantity: number;
    unitPrice: number;
    total: number;
  }