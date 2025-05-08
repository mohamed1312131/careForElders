import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import  { catchError, Observable, of, tap } from "rxjs"
import { environment } from "src/environments/environment"

@Injectable({
  providedIn: "root",
})
export class PatientBillService {
  // Update the API URL to match your backend
  private apiUrl = `${environment.apiUrl}/bills`

  constructor(private http: HttpClient) {
    console.log("PatientBillService initialized with API URL:", this.apiUrl)
  }

  // HTTP request options
  private httpOptions = {
    headers: new HttpHeaders({
      "Content-Type": "application/json",
      // Add any auth headers if needed
      // 'Authorization': 'Bearer ' + localStorage.getItem('token')
    }),
  }

  getAllBills(): Observable<any[]> {
    console.log("Fetching all bills from:", this.apiUrl)
    return this.http.get<any[]>(this.apiUrl).pipe(
      tap((_) => console.log("Fetched bills")),
      catchError(this.handleError<any[]>("getAllBills", [])),
    )
  }

  getBillsByPatientId(patientId: number): Observable<any[]> {
    const url = `${this.apiUrl}/patient/${patientId}`
    console.log(`Fetching bills for patient ${patientId} from:`, url)
    return this.http.get<any[]>(url).pipe(
      tap((_) => console.log(`Fetched bills for patient id=${patientId}`)),
      catchError(this.handleError<any[]>(`getBillsByPatientId id=${patientId}`, [])),
    )
  }

  getBillById(id: string): Observable<any> {
    const url = `${this.apiUrl}/${id}`
    console.log(`Fetching bill ${id} from:`, url)
    return this.http.get<any>(url).pipe(
      tap((_) => console.log(`Fetched bill id=${id}`)),
      catchError(this.handleError<any>(`getBillById id=${id}`)),
    )
  }

  createBill(bill: any): Observable<any> {
    console.log("Creating bill with data:", bill)
    console.log("API URL:", this.apiUrl)

    // Format the data to match the backend API structure
    const formattedBill = {
      id: bill.id,
      billNumber: bill.billNumber,
      patientId: bill.patientId,
      patientName: bill.patientName,
      patientEmail: bill.patientEmail || "",
      patientPhone: bill.patientPhone || "",
      billDate: bill.billDate,
      dueDate: bill.dueDate,
      totalAmount: bill.totalAmount,
      paidAmount: bill.paidAmount || 0,
      balanceAmount: bill.balanceAmount || bill.totalAmount,
      status: bill.paymentStatus,
      items: bill.items.map((item: any) => ({
        id: item.id,
        description: item.description,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        serviceDate: bill.billDate, // Default to bill date if not provided
      })),
      notes: bill.notes || "",
    }

    console.log("Formatted bill data:", formattedBill)



    // Make sure to return the HTTP request
    return this.http.post<any>(this.apiUrl, formattedBill, this.httpOptions).pipe(
      tap((newBill: any) => console.log(`Created bill w/ id=${newBill.id}`)),
      catchError(this.handleError<any>("createBill")),
    )
  }

  updateBill(id: string, bill: any): Observable<any> {
    const url = `${this.apiUrl}/${id}`
    console.log(`Updating bill ${id} with data:`, bill)

    // Format the data to match the backend API structure
    const formattedBill = {
      id: id,
      billNumber: bill.billNumber,
      patientId: bill.patientId,
      patientName: bill.patientName,
      patientEmail: bill.patientEmail || "",
      patientPhone: bill.patientPhone || "",
      billDate: bill.billDate,
      dueDate: bill.dueDate,
      totalAmount: bill.totalAmount,
      paidAmount: bill.paidAmount || 0,
      balanceAmount: bill.balanceAmount || bill.totalAmount,
      status: bill.paymentStatus,
      items: bill.items.map((item: any) => ({
        id: item.id,
        description: item.description,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        serviceDate: bill.billDate, // Default to bill date if not provided
      })),
      notes: bill.notes || "",
    }

    
   

    return this.http.put<any>(url, formattedBill, this.httpOptions).pipe(
      tap((_) => console.log(`Updated bill id=${id}`)),
      catchError(this.handleError<any>("updateBill")),
    )
  }

  deleteBill(id: string): Observable<void> {
    const url = `${this.apiUrl}/${id}`
    console.log(`Deleting bill ${id}`)
    return this.http.delete<void>(url, this.httpOptions).pipe(
      tap((_) => console.log(`Deleted bill id=${id}`)),
      catchError(this.handleError<void>("deleteBill")),
    )
  }

  generatePdf(id: string): Observable<Blob> {
    const url = `${this.apiUrl}/${id}/pdf`
    console.log(`Generating PDF for bill ${id}`)
    return this.http
      .get(url, {
        responseType: "blob",
      })
      .pipe(
        tap((_) => console.log(`Generated PDF for bill id=${id}`)),
        catchError(this.handleError<Blob>("generatePdf")),
      )
  }

  // Error handling method
  private handleError<T>(operation = "operation", result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error)

      // Let the app keep running by returning an empty result
      return of(result as T)
    }
  }

  
}
