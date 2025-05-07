export interface BillItem {
    id?: number
    description: string
    quantity: number
    unitPrice: number
    serviceDate: Date
    amount?: number
  }
  
  export interface PatientBill {
    id?: number
    billNumber: string
    patientId: number
    patientName: string
    patientEmail?: string
    patientPhone?: string
    billDate: Date
    dueDate: Date
    totalAmount: number
    paidAmount?: number
    balanceAmount?: number
    status: string
    items: BillItem[]
    notes?: string
  }
  