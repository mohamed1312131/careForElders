import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http"
import { catchError, Observable, of, tap, map } from "rxjs"
//import { environment } from "src/environments/environment"

@Injectable({
  providedIn: "root",
})
export class PatientBillService {
  // Update the API URL to match your backend
  private apiUrl = `http://localhost:8085/api/bills`
  private paymentApiUrl = `http://localhost:8085/api/payments`

  constructor(private http: HttpClient) {
    console.log("PatientBillService initialized with API URL:", this.apiUrl)
    console.log("Payment API URL:", this.paymentApiUrl)
  }

  // HTTP request options
  private httpOptions = {
    headers: new HttpHeaders({
      "Content-Type": "application/json",
    }),
  }

  // ==================== BILL MANAGEMENT METHODS ====================

  getAllBills(): Observable<any[]> {
    console.log("Fetching all bills from:", this.apiUrl)
    return this.http.get<any[]>(this.apiUrl).pipe(
      map((bills) => bills.map((bill) => this.transformBillData(bill))),
      tap((bills) => console.log("Fetched and transformed bills:", bills.length)),
      catchError(this.handleError<any[]>("getAllBills", [])),
    )
  }

  // ==================== NEW METHOD: GET BILLS BY SERVICE TYPE ====================
  getBillsByServiceType(serviceType: string): Observable<any[]> {
    const url = `${this.apiUrl}/service-type/${serviceType}`;
    console.log(`Fetching bills for service type ${serviceType} from:`, url);
    return this.http.get<any[]>(url).pipe(
      map((bills) => bills.map((bill) => this.transformBillData(bill))),
      tap((bills) => console.log(`Fetched ${bills.length} bills for service type=${serviceType}`)),
      catchError(this.handleError<any[]>(`getBillsByServiceType serviceType=${serviceType}`, [])),
    );
  }

  getBillsByPatientId(patientId: number): Observable<any[]> {
    const url = `${this.apiUrl}/patient/${patientId}`
    console.log(`Fetching bills for patient ${patientId} from:`, url)
    return this.http.get<any[]>(url).pipe(
      map((bills) => bills.map((bill) => this.transformBillData(bill))),
      tap((bills) => console.log(`Fetched ${bills.length} bills for patient id=${patientId}`)),
      catchError(this.handleError<any[]>(`getBillsByPatientId id=${patientId}`, [])),
    )
  }

  getBillById(id: string): Observable<any> {
    const url = `${this.apiUrl}/${id}`
    console.log(`Fetching bill ${id} from:`, url)
    return this.http.get<any>(url).pipe(
      map((bill) => this.transformBillData(bill)),
      tap((bill) => console.log(`Fetched and transformed bill id=${id}:`, bill)),
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
      status: bill.status || bill.paymentStatus,
      serviceType: bill.serviceType || "DOCTOR_CARE", // Ensure service type is included
      items: bill.items.map((item: any) => ({
        id: item.id,
        serviceType: item.serviceType || bill.serviceType || "DOCTOR_CARE",
        description: item.description,
        quantity: Number(item.quantity),
        unitPrice: Number(item.unitPrice),
        unit: item.unit || "Session",
      })),
      notes: bill.notes || "",
    }

    console.log("Formatted bill data:", formattedBill)

    // Make sure to return the HTTP request
    return this.http.post<any>(this.apiUrl, formattedBill, this.httpOptions).pipe(
      map((newBill) => this.transformBillData(newBill)),
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
      status: bill.status || bill.paymentStatus,
      serviceType: bill.serviceType || "DOCTOR_CARE", // Ensure service type is included
      items: bill.items.map((item: any) => ({
        id: item.id,
        description: item.description,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        serviceType: item.serviceType || bill.serviceType || "DOCTOR_CARE",
        serviceDate: item.serviceDate || bill.billDate, // Use item service date if available
      })),
      notes: bill.notes || "",
    }

    return this.http.put<any>(url, formattedBill, this.httpOptions).pipe(
      map((updatedBill) => this.transformBillData(updatedBill)),
      tap((updatedBill) => console.log(`Updated bill id=${id}:`, updatedBill)),
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

  // ==================== DATA TRANSFORMATION METHODS ====================

  /**
   * Transform bill data from backend format to frontend format
   * Ensures consistent data structure and handles missing fields
   */
  private transformBillData(bill: any): any {
    if (!bill) return bill;

    // Ensure serviceType is properly set
    let serviceType = bill.serviceType;

    // If serviceType is missing, try to derive it from items
    if (!serviceType || serviceType === 'N/A') {
      if (bill.items && bill.items.length > 0) {
        // Use the service type from the first item
        serviceType = bill.items[0].serviceType || 'DOCTOR_CARE';
      } else {
        serviceType = 'DOCTOR_CARE'; // Default fallback
      }
    }

    return {
      ...bill,
      serviceType: serviceType,
      // Ensure other required fields have defaults
      status: bill.status || 'PENDING',
      paidAmount: bill.paidAmount || 0,
      balanceAmount: bill.balanceAmount || bill.totalAmount || 0,
      patientEmail: bill.patientEmail || '',
      patientPhone: bill.patientPhone || '',
      notes: bill.notes || '',
      items: bill.items ? bill.items.map((item: any) => ({
        ...item,
        serviceType: item.serviceType || serviceType,
        unit: item.unit || 'Session'
      })) : []
    };
  }

  /**
   * Get display name for service type
   */
  getServiceTypeDisplay(serviceType: string): string {
    if (!serviceType || serviceType === 'N/A') return 'N/A';

    switch (serviceType.toUpperCase()) {
      case 'DOCTOR_CARE':
        return 'Doctor Care';
      case 'PARA_MEDICAL_SERVICES':
        return 'Para Medical';
      case 'SUBSCRIPTION':
        return 'Subscription';
      default:
        return serviceType.replace(/_/g, ' ').toLowerCase()
          .replace(/\b\w/g, l => l.toUpperCase());
    }
  }

  /**
   * Get icon for service type
   */
  getServiceTypeIcon(serviceType: string): string {
    if (!serviceType || serviceType === 'N/A') return 'help_outline';

    switch (serviceType.toUpperCase()) {
      case 'DOCTOR_CARE':
        return 'medical_services';
      case 'PARA_MEDICAL_SERVICES':
        return 'healing';
      case 'SUBSCRIPTION':
        return 'card_membership';
      default:
        return 'receipt';
    }
  }

  /**
   * Get color for service type
   */
  getServiceTypeColor(serviceType: string): string {
    if (!serviceType || serviceType === 'N/A') return '#9e9e9e';

    switch (serviceType.toUpperCase()) {
      case 'DOCTOR_CARE':
        return '#2196F3';
      case 'PARA_MEDICAL_SERVICES':
        return '#4CAF50';
      case 'SUBSCRIPTION':
        return '#FF9800';
      default:
        return '#9e9e9e';
    }
  }

  // ==================== PAYMENT MANAGEMENT METHODS ====================

  /**
   * Get all payments for a specific bill
   * @param billId The ID of the bill
   * @returns Observable of payment array
   */
  getBillPayments(billId: string): Observable<any[]> {
    const url = `${this.apiUrl}/${billId}/payments`
    console.log(`Fetching payments for bill ${billId} from:`, url)
    return this.http.get<any[]>(url).pipe(
      tap((payments) => console.log(`Fetched ${payments.length} payments for bill id=${billId}`)),
      catchError(this.handleError<any[]>(`getBillPayments billId=${billId}`, [])),
    )
  }

  /**
   * Get payments for a bill with a specific status
   * @param billId The ID of the bill
   * @param status The payment status to filter by
   * @returns Observable of payment array
   */
  getBillPaymentsByStatus(billId: string, status: string): Observable<any[]> {
    const url = `${this.apiUrl}/${billId}/payments/status/${status}`
    console.log(`Fetching ${status} payments for bill ${billId} from:`, url)
    return this.http.get<any[]>(url).pipe(
      tap((payments) => console.log(`Fetched ${payments.length} ${status} payments for bill id=${billId}`)),
      catchError(this.handleError<any[]>(`getBillPaymentsByStatus billId=${billId} status=${status}`, [])),
    )
  }

  /**
   * Create a new payment for a bill
   * @param billId The ID of the bill
   * @param paymentData The payment data
   * @returns Observable of the created payment
   */
  createPayment(billId: string, paymentData: any): Observable<any> {
    const url = `${this.apiUrl}/${billId}/payments`
    console.log(`Creating payment for bill ${billId} with data:`, paymentData)
    return this.http.post<any>(url, paymentData, this.httpOptions).pipe(
      tap((payment) => console.log(`Created payment w/ id=${payment.paymentId} for bill id=${billId}`)),
      catchError(this.handleError<any>(`createPayment billId=${billId}`)),
    )
  }

  /**
   * Process a payment with a specific payment method
   * @param paymentId The ID of the payment
   * @param paymentMethod The payment method (CASH or ONLINE)
   * @returns Observable of the processed payment
   */
  processPayment(paymentId: string, paymentMethod: string): Observable<any> {
    const url = `${this.paymentApiUrl}/process/${paymentId}`
    console.log(`Processing payment ${paymentId} with method ${paymentMethod}`)

    const params = new HttpParams().set("paymentMethod", paymentMethod)

    return this.http.post<any>(url, null, { params }).pipe(
      tap((result) => console.log(`Processed payment id=${paymentId} with result:`, result)),
      catchError(this.handleError<any>(`processPayment paymentId=${paymentId}`)),
    )
  }

  /**
   * Simulate an online payment (for testing)
   * @param paymentId The ID of the payment
   * @param success Whether the payment should succeed (default: true)
   * @returns Observable of the payment result
   */
  simulateOnlinePayment(paymentId: string, success = true): Observable<any> {
    const url = `${this.paymentApiUrl}/simulator/${paymentId}`
    console.log(`Simulating ${success ? "successful" : "failed"} online payment for ${paymentId}`)

    const params = new HttpParams().set("success", success.toString())

    return this.http.post<any>(url, null, { params }).pipe(
      tap((result) => console.log(`Simulated payment id=${paymentId} with result:`, result)),
      catchError(this.handleError<any>(`simulateOnlinePayment paymentId=${paymentId}`)),
    )
  }

  /**
   * Get a specific payment by ID
   * @param paymentId The ID of the payment
   * @returns Observable of the payment
   */
  getPaymentById(paymentId: string): Observable<any> {
    const url = `${this.paymentApiUrl}/${paymentId}`
    console.log(`Fetching payment ${paymentId} from:`, url)
    return this.http.get<any>(url).pipe(
      tap((_) => console.log(`Fetched payment id=${paymentId}`)),
      catchError(this.handleError<any>(`getPaymentById id=${paymentId}`)),
    )
  }

  /**
   * Get all payments with optional filtering
   * @param status Optional status filter
   * @param method Optional payment method filter
   * @returns Observable of payment array
   */
  getAllPayments(status?: string, method?: string): Observable<any[]> {
    const url = this.paymentApiUrl
    let params = new HttpParams()

    if (status) params = params.set("status", status)
    if (method) params = params.set("method", method)

    console.log(`Fetching all payments with filters:`, { status, method })

    return this.http.get<any[]>(url, { params }).pipe(
      tap((payments) => console.log(`Fetched ${payments.length} payments`)),
      catchError(this.handleError<any[]>("getAllPayments", [])),
    )
  }

  /**
   * Download a payment receipt as PDF
   * @param paymentId The ID of the payment
   * @returns Observable of the receipt blob
   */
  getPaymentReceipt(paymentId: string): Observable<Blob> {
    const url = `${this.paymentApiUrl}/${paymentId}/receipt`
    console.log(`Getting receipt for payment ${paymentId} from:`, url)

    return this.http
      .get(url, {
        responseType: "blob",
      })
      .pipe(
        tap((_) => console.log(`Generated receipt for payment id=${paymentId}`)),
        catchError(this.handleError<Blob>("getPaymentReceipt")),
      )
  }

  /**
   * Download a payment receipt as PDF
   * @param paymentId The ID of the payment
   */
  downloadPaymentReceipt(paymentId: string): void {
    const url = `${this.paymentApiUrl}/${paymentId}/receipt`
    console.log(`Downloading receipt for payment ${paymentId} from:`, url)

    // Open in a new tab/window
    window.open(url, "_blank")
  }

  /**
   * Download a bill invoice as PDF
   * @param billId The ID of the bill
   */
  downloadBillPdf(billId: string): void {
    const url = `${this.apiUrl}/${billId}/pdf`
    console.log(`Downloading PDF for bill ${billId} from:`, url)

    // Open in a new tab/window
    window.open(url, "_blank")
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
