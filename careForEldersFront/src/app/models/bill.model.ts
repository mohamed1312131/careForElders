export interface Bill {
  id?: number
  patientId: number
  patientName: string
  billDate: string
  dueDate: string
  totalAmount: number
  status: string
  items: BillItem[]
  description?: string
}

export interface BillItem {
  id?: number
  description: string
  quantity: number
  unitPrice: number
  amount: number
}
