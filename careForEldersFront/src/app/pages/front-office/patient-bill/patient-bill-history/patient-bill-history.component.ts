import { Component,  OnInit } from "@angular/core"
import  { ActivatedRoute, Router } from "@angular/router"
//import  { PatientBillService } from "../../../../patient-bill.service"
import  { ToastrService } from "ngx-toastr"
import { catchError, finalize, tap } from "rxjs/operators"
import { forkJoin, of } from "rxjs"
import { PatientBillService } from "../patient-bill.service"
@Component({
  selector: "app-patient-bill-history",
  templateUrl: "./patient-bill-history.component.html",
  styleUrls: ["./patient-bill-history.component.scss"],
})
export class PatientBillHistoryComponent implements OnInit {
  billId = "" // Initialize with empty string to fix TypeScript error
  bill: any = null // Initialize with null
  payments: any[] = []
  isLoading = false
  errorMessage = ""

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private billService: PatientBillService,
    private toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get("id")
    if (id) {
      this.billId = id
      console.log(`Initializing payment history component for bill ID: ${this.billId}`)
      this.loadData()
    } else {
      this.errorMessage = "Bill ID not found in route parameters"
      this.toastr.error(this.errorMessage)
      this.router.navigate(["/user/userProfile/patient-bill"])
    }
  }

  loadData(): void {
    if (!this.billId) {
      this.errorMessage = "Bill ID is required"
      this.toastr.error(this.errorMessage)
      return
    }

    console.log(`Loading bill details and payment history for ID: ${this.billId}`)
    this.isLoading = true

    // Use forkJoin to load both bill details and payment history in parallel
    forkJoin({
      bill: this.billService.getBillById(this.billId).pipe(
        catchError((error) => {
          console.error("Error loading bill details:", error)
          this.toastr.error("Failed to load bill details")
          return of(null)
        }),
      ),
      payments: this.billService.getBillPayments(this.billId).pipe(
        catchError((error) => {
          console.error("Error loading payment history:", error)
          this.toastr.error("Failed to load payment history")
          return of([])
        }),
      ),
    })
      .pipe(
        tap((result) => {
          console.log("Data loaded successfully:", result)
          this.bill = result.bill
          this.payments = result.payments || [] // Ensure payments is always an array

          // Sort payments by date (newest first)
          if (this.payments && this.payments.length > 0) {
            this.payments.sort((a, b) => {
              return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
            })
            console.log(`Sorted ${this.payments.length} payments by date`)
          }
        }),
        finalize(() => {
          this.isLoading = false
        }),
      )
      .subscribe()
  }

  getStatusClass(status: string): string {
    if (!status) return "badge-secondary"

    switch (status) {
      case "COMPLETED":
        return "badge-success"
      case "PENDING":
        return "badge-warning"
      case "FAILED":
        return "badge-danger"
      default:
        return "badge-secondary"
    }
  }

  downloadReceipt(paymentId: string): void {
    if (!paymentId) {
      this.toastr.error("Payment ID is required")
      return
    }

    console.log(`Downloading receipt for payment ID: ${paymentId}`)
    try {
      this.billService.downloadPaymentReceipt(paymentId)
      this.toastr.success("Receipt download initiated")
    } catch (error) {
      console.error("Error downloading receipt:", error)
      this.toastr.error("Failed to download receipt")
    }
  }

  makePayment(): void {
    if (!this.billId) {
      this.toastr.error("Bill ID is required")
      return
    }

    console.log(`Navigating to payment page for bill ID: ${this.billId}`)
    this.router.navigate(["/user/userProfile/patient-bill/payment", this.billId])
  }

  viewBillDetails(): void {
    if (!this.billId) {
      this.toastr.error("Bill ID is required")
      return
    }

    console.log(`Navigating to bill details for ID: ${this.billId}`)
    this.router.navigate(["/user/userProfile/patient-bill", this.billId])
  }

  backToBills(): void {
    console.log("Navigating back to bills list")
    this.router.navigate(["/user/userProfile/patient-bill"])
  }

  formatDate(dateString: string): string {
    if (!dateString) return "N/A"
    try {
      const date = new Date(dateString)
      return date.toLocaleString()
    } catch (error) {
      console.error("Error formatting date:", error)
      return "Invalid Date"
    }
  }

  refreshPayments(): void {
    if (!this.billId) {
      this.toastr.error("Bill ID is required")
      return
    }

    console.log(`Refreshing payment history for bill ID: ${this.billId}`)
    this.isLoading = true

    this.billService
      .getBillPayments(this.billId)
      .pipe(
        tap((payments) => {
          console.log(`Loaded ${payments.length} payments`)
          this.payments = payments || [] // Ensure payments is always an array

          // Sort payments by date (newest first)
          if (this.payments && this.payments.length > 0) {
            this.payments.sort((a, b) => {
              return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
            })
          }

          this.toastr.success("Payment history refreshed")
        }),
        catchError((error) => {
          console.error("Error refreshing payment history:", error)
          this.toastr.error("Failed to refresh payment history")
          return of([])
        }),
        finalize(() => {
          this.isLoading = false
        }),
      )
      .subscribe()
  }
}
