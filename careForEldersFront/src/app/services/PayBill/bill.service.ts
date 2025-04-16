// src/app/services/bill.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Bill,  BillItem } from 'src/app/entities/bill';
import {Invoice} from 'src/app/entities/invoice'

@Injectable({
  providedIn: 'root'
})
export class BillService {
  private apiUrl = 'http://localhost:8080/api/bills';

  constructor(private http: HttpClient) { }

  // Basic Bill Operations
  getBill(id: number): Observable<Bill> {
    return this.http.get<Bill>(`${this.apiUrl}/${id}`);
  }

  getBillsByPatient(patientId: number): Observable<Bill[]> {
    return this.http.get<Bill[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  createBill(billData: Partial<Bill>): Observable<Bill> {
    return this.http.post<Bill>(this.apiUrl, billData);
  }

  updateBill(id: number, updates: Partial<Bill>): Observable<Bill> {
    return this.http.patch<Bill>(`${this.apiUrl}/${id}`, updates);
  }

  // Invoice-Specific Operations
  generateInvoice(billId: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.apiUrl}/${billId}/generate-invoice`, {});
  }

  getInvoice(billId: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.apiUrl}/${billId}/invoice`);
  }

  sendInvoiceByEmail(billId: number, email?: string): Observable<void> {
    const payload = email ? { email } : {};
    return this.http.post<void>(`${this.apiUrl}/${billId}/send-invoice`, payload);
  }

  // Bill Items Management
  addBillItem(billId: number, item: BillItem): Observable<Bill> {
    return this.http.post<Bill>(`${this.apiUrl}/${billId}/items`, item);
  }

  updateBillItem(billId: number, itemId: number, updates: Partial<BillItem>): Observable<Bill> {
    return this.http.patch<Bill>(`${this.apiUrl}/${billId}/items/${itemId}`, updates);
  }

  removeBillItem(billId: number, itemId: number): Observable<Bill> {
    return this.http.delete<Bill>(`${this.apiUrl}/${billId}/items/${itemId}`);
  }

  // Payment Operations
  recordPayment(billId: number, paymentData: { amount: number, method: string }): Observable<Bill> {
    return this.http.post<Bill>(`${this.apiUrl}/${billId}/payments`, paymentData);
  }

  // Status Operations
  markAsPaid(billId: number): Observable<Bill> {
    return this.http.patch<Bill>(`${this.apiUrl}/${billId}/status`, { status: 'PAID' });
  }

  // Utility Methods
  getBillTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/types`);
  }

  getPaymentMethods(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/payment-methods`);
  }
}