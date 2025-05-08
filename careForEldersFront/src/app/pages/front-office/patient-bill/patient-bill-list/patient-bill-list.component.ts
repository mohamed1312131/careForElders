import { Component, OnInit, ViewChild } from "@angular/core"
import { MatPaginator } from "@angular/material/paginator"
import { MatSort } from "@angular/material/sort"
import { MatTableDataSource } from "@angular/material/table"
import  { MatSnackBar } from "@angular/material/snack-bar"
import { Router } from "@angular/router"
import  { PatientBillService } from "../patient-bill.service"

@Component({
  selector: "app-patient-bill-list",
  templateUrl: "./patient-bill-list.component.html",
  styleUrls: ["./patient-bill-list.component.scss"],
})
export class PatientBillListComponent implements OnInit {
  displayedColumns: string[] = ["id", "patientName", "billDate", "totalAmount", "paymentStatus", "actions"]
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
    switch (status.toUpperCase()) {
      case "PAID":
        return "var(--status-success)"
      case "PENDING":
        return "var(--status-warning)"
      case "OVERDUE":
        return "var(--status-danger)"
      case "CANCELLED":
        return "var(--status-neutral)"
      default:
        return "var(--status-neutral)"
    }
  }

  createNewBill(): void {
    console.log("Navigating to create bill form")
    // Fix: Use the correct route path based on your application's URL structure
    this.router.navigate(["/bill/create"])
  }

  editBill(id: string): void {
    this.router.navigate(["/bill/edit", id])
  }

  viewBillDetails(id: string): void {
    this.router.navigate(["/bill/view", id])
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
}