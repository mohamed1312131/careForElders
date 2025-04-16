import { Bill } from "./bill";

export interface Invoice extends Bill {
  invoiceNumber: string;
  billingAddress?: string;
  shippingAddress?: string;
  terms?: string;
  notes?: string;
  taxId?: string;
  footerMessage?: string;
}
export interface Patient {
  id: number;
  name: string;
  email: string;
  phone?: string;
  address?: string;
}