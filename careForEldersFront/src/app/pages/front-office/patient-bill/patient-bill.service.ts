import { Injectable } from "@angular/core"
import { HttpClient } from "@angular/common/http"
import  { Observable } from "rxjs"
import { environment } from "src/environments/environment"

@Injectable({
  providedIn: "root",
})
export class PatientBillService {
  private apiUrl = `${environment.apiUrl}/bills`

  constructor(private http: HttpClient) {}

  getAllBills(): Observable<any[]> {
    console.log("Fetching all bills from:", this.apiUrl)
    return this.http.get<any[]>(this.apiUrl)
  }

  getBillsByPatientId(patientId: number): Observable<any[]> {
    console.log(`Fetching bills for patient ${patientId} from: ${this.apiUrl}/patient/${patientId}`)
    return this.http.get<any[]>(`${this.apiUrl}/patient/${patientId}`)
  }

  getBillById(id: string): Observable<any> {
    console.log(`Fetching bill ${id} from: ${this.apiUrl}/${id}`)
    return this.http.get<any>(`${this.apiUrl}/${id}`)
  }

  createBill(bill: any): Observable<any> {
    console.log("Creating bill with data:", bill)
    console.log("API URL:", this.apiUrl)
    return this.http.post<any>(this.apiUrl, bill)
  }

  updateBill(id: string, bill: any): Observable<any> {
    console.log(`Updating bill ${id} with data:`, bill)
    return this.http.put<any>(`${this.apiUrl}/${id}`, bill)
  }

  deleteBill(id: string): Observable<void> {
    console.log(`Deleting bill ${id}`)
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }

  generatePdf(id: string): Observable<Blob> {
    console.log(`Generating PDF for bill ${id}`)
    return this.http.get(`${this.apiUrl}/${id}/pdf`, {
      responseType: "blob",
    })
  }
}
