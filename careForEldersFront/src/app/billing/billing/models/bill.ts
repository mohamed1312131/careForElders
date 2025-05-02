import { ServiceItem } from "./service-item";

export interface Bill {
    id: number;
    patientId: number;
    patientName: string;
    billDate: string;
    dueDate?: string;
    services: ServiceItem[];
    totalAmount: number;
    status: BillStatus;
    notes?: string;
  }
  
  export type BillStatus = 'DRAFT' | 'PENDING' | 'SENT' | 'PAID' | 'OVERDUE' | 'CANCELLED';