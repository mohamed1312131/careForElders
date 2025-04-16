import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Payment } from "src/app/entities/payment";

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = 'http://localhost:8080/api/payments';

  constructor(private http: HttpClient) {}

  processPayment(billId: number, method: 'ONLINE' | 'CASH'): Observable<Payment> {
    return this.http.post<Payment>(this.apiUrl, { billId, method });
  }
}