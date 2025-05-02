import { Component, type OnInit } from "@angular/core"
import type { Bill } from "src/app/models/bill.model"
import type { BillService } from "src/app/services/bill.service"
import type { Router } from "@angular/router"

@Component({
  selector: "app-bill-list",
  templateUrl: "./bill-list.component.html",
  styleUrls: ["./bill-list.component.scss"],
})
export class BillListComponent implements OnInit {
  bills: Bill[] = []
  loading = true
  error = ""

  constructor(
    private billService: BillService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadBills()
  }

  loadBills(): void {
    this.loading = true
    this.billService.getAllBills().subscribe({
      next: (data) => {
        this.bills = data
        this.loading = false
      },
      error: (err) => {
        this.error = "Failed to load bills. Please try again later."
        this.loading = false
        console.error(err)
      },
    })
  }

  viewBill(id: number): void {
    this.router.navigate(["/bills", id])
  }

  editBill(id: number): void {
    this.router.navigate(["/bills", id, "edit"])
  }

  deleteBill(id: number): void {
    if (confirm("Are you sure you want to delete this bill?")) {
      this.billService.deleteBill(id).subscribe({
        next: () => {
          this.loadBills()
        },
        error: (err) => {
          this.error = "Failed to delete bill. Please try again later."
          console.error(err)
        },
      })
    }
  }

  downloadPdf(id: number): void {
    this.billService.downloadPdf(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement("a")
        a.href = url
        a.download = `invoice_${id}.pdf`
        document.body.appendChild(a)
        a.click()
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
      },
      error: (err) => {
        this.error = "Failed to download PDF. Please try again later."
        console.error(err)
      },
    })
  }

  createNewBill(): void {
    this.router.navigate(["/bills/new"])
  }

  getBadgeClass(status: string): string {
    switch (status) {
      case "PAID":
        return "badge bg-success"
      case "UNPAID":
        return "badge bg-warning"
      case "OVERDUE":
        return "badge bg-danger"
      default:
        return "badge bg-secondary"
    }
  }
}
