import { Component, OnInit, ViewChild } from "@angular/core"
import { MatPaginator } from "@angular/material/paginator"
import { MatSort } from "@angular/material/sort"
import { MatTableDataSource } from "@angular/material/table"
import  { MatSnackBar } from "@angular/material/snack-bar"
import { Router } from "@angular/router"
import { PatientBillService } from "../patient-bill.service"

@Component({
  selector: "app-patient-bill-list",
  templateUrl: "./patient-bill-list.component.html",
  styleUrls: ["./patient-bill-list.component.scss"],
})
export class PatientBillListComponent implements OnInit {
  // Add 'payment' to the displayed columns
  displayedColumns: string[] = [
    "billNumber",
    "patientName",
    "billDate",
    "dueDate",
    "totalAmount",
    "status",
    "actions",
    "paymentActions",
  ]
  dataSource = new MatTableDataSource<any>([])
  isLoading = true
  noDataMessage = "No bills available. Create a new bill to get started."

  @ViewChild(MatPaginator) paginator!: MatPaginator
  @ViewChild(MatSort) sort!: MatSort

  constructor(
    private patientBillService: PatientBillService,
    private snackBar: MatSnackBar,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadPatientBills()
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator
    this.dataSource.sort = this.sort
  }

  loadPatientBills(): void {
    this.isLoading = true
    this.patientBillService.getAllBills().subscribe({
      next: (bills) => {
        this.dataSource.data = bills
        this.isLoading = false
      },
      error: (error) => {
        console.error("Error fetching patient bills:", error)
        this.snackBar.open("Failed to load patient bills", "Close", {
          duration: 5000,
          horizontalPosition: "end",
          verticalPosition: "top",
          panelClass: ["error-snackbar"],
        })
        this.isLoading = false
      },
    })
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSource.filter = filterValue.trim().toLowerCase()

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage()
    }
  }

  getStatusColor(status: string): string {
    if (!status) return "var(--status-neutral)"

    switch (status.toUpperCase()) {
      case "PAID":
        return "var(--status-success)"
      case "PARTIALLY_PAID":
      case "PAYMENT_PLAN":
        return "var(--status-info)"
      case "PENDING":
      case "PENDING_INSURANCE":
      case "SENT":
        return "var(--status-warning)"
      case "OVERDUE":
      case "DISPUTED":
        return "var(--status-danger)"
      case "UNPAID":
        return "var(--status-danger)"
      case "CANCELLED":
      case "ADJUSTED":
        return "var(--status-neutral)"
      default:
        return "var(--status-neutral)"
    }
  }

  formatDate(dateString: string | Date | null): string {
    if (!dateString) return "N/A"
    return new Date(dateString).toLocaleDateString()
  }

  formatCurrency(amount: number | string | null): string {
    if (amount === null || amount === undefined) return "N/A"

    // Convert string to number if needed
    const numericAmount = typeof amount === "string" ? Number.parseFloat(amount) : amount

    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(numericAmount)
  }

  getDisplayValue(value: any, defaultValue = "N/A"): string {
    return value !== null && value !== undefined ? value : defaultValue
  }

  createNewBill(): void {
    console.log("Navigating to create bill form")
    this.router.navigate(["/bill/create"])
  }

  editBill(id: string): void {
    this.router.navigate(["/bill/edit", id])
  }

  viewBillDetails(id: string): void {
    this.router.navigate(["/bill/view", id])
  }

  // Add new method to navigate to payment view
  navigateToPayment(id: string): void {
    console.log("Navigating to payment view for bill:", id)
    this.router.navigate(["/bill/payment", id])
  }

  // Add method to check if a bill is eligible for payment before the deletePatientBill method
  isPaymentEligible(bill: any): boolean {
    if (!bill || !bill.status) return false

    const payableStatuses = ["UNPAID", "OVERDUE", "PARTIALLY_PAID", "PENDING", "SENT"]
    return payableStatuses.includes(bill.status.toUpperCase())
  }

  deletePatientBill(id: string): void {
    if (confirm("Are you sure you want to delete this bill?")) {
      this.patientBillService.deleteBill(id).subscribe({
        next: () => {
          this.loadPatientBills()
          this.snackBar.open("Patient bill deleted successfully", "Close", {
            duration: 3000,
            horizontalPosition: "end",
            verticalPosition: "top",
          })
        },
        error: (error) => {
          console.error("Error deleting patient bill:", error)
          this.snackBar.open("Failed to delete patient bill", "Close", {
            duration: 5000,
            horizontalPosition: "end",
            verticalPosition: "top",
            panelClass: ["error-snackbar"],
          })
        },
      })
    }
  }

  downloadPdf(id: string): void {
    this.patientBillService.generatePdf(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement("a")
        a.href = url
        a.download = `bill_${id}.pdf`
        document.body.appendChild(a)
        a.click()
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
      },
      error: (error) => {
        console.error("Error generating PDF:", error)
        this.snackBar.open("Failed to generate PDF", "Close", {
          duration: 5000,
          horizontalPosition: "end",
          verticalPosition: "top",
          panelClass: ["error-snackbar"],
        })
      },
    })
  }

  // Check if bill is eligible for payment based on status
  canProcessPayment(status: string | null | undefined): boolean {
    if (!status) return false

    const payableStatuses = ["UNPAID", "OVERDUE", "PARTIALLY_PAID", "PENDING", "SENT"]
    return payableStatuses.includes(status.toUpperCase())
  }
}
