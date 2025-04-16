// src/app/services/invoice.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Invoice } from 'src/app/entities/invoice';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private apiUrl = 'http://localhost:8080/api/invoices';

  constructor(private http: HttpClient) {}

  generateInvoice(billId: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.apiUrl}/generate`, { billId });
  }

  getInvoice(invoiceId: string): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.apiUrl}/${invoiceId}`);
  }

  sendEmail(invoiceId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${invoiceId}/send`, {});
  }
}