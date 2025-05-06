import { Injectable } from "@angular/core"
import type { HttpClient } from "@angular/common/http"
import type { Observable } from "rxjs"
import { Bill } from "src/app/models/bill.model"
//import type { Bill } from "../models/bill.model"

@Injectable({
  providedIn: "root",
})
export class BillService {
  private apiUrl = "http://localhost:8080/api/bills"

  constructor(private http: HttpClient) {}

  getAllBills(): Observable<Bill[]> {
    return this.http.get<Bill[]>(this.apiUrl)
  }

  getBillById(id: number): Observable<Bill> {
    return this.http.get<Bill>(`${this.apiUrl}/${id}`)
  }

  createBill(bill: Bill): Observable<Bill> {
    return this.http.post<Bill>(this.apiUrl, bill)
  }

  updateBill(id: number, bill: Bill): Observable<Bill> {
    return this.http.put<Bill>(`${this.apiUrl}/${id}`, bill)
  }

  deleteBill(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }

  downloadPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, {
      responseType: "blob",
    })
  }
}
