// src/app/billing/services/bill.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Bill } from '../../../models/bill.model';
import { ServiceItem } from '../../../models/service-item';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BillService {
  // Direct API URL (replace with your actual backend URL)
  private apiUrl = 'http://localhost:8080/api/bills';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Bill[]> {
    return this.http.get<Bill[]>(this.apiUrl);
  }

  create(bill: Bill): Observable<Bill> {
    // Calculate total if not provided
  /*  if (!bill.totalAmount && bill.services) {
      bill.totalAmount = bill.services.reduce(
        (sum, item) => sum + (item.rate * item.quantity), 0
      );
    }*/
    return this.http.post<Bill>(this.apiUrl, bill);
  }

  generatePdf(billId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${billId}/pdf`, {
      responseType: 'blob'
    });
  }

  // Helper method for service items
  calculateServiceTotal(services: ServiceItem[]): number {
    return services.reduce(
      (total, item) => total + (item.rate * item.quantity), 0
    );
  }
}