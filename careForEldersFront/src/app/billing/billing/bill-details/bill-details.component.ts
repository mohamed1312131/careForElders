import { Component, type OnInit } from "@angular/core"
import type { ActivatedRoute, Router } from "@angular/router"
import type { Bill } from "src/app/models/bill.model"
import type { BillService } from "src/app/services/bill.service"

@Component({
  selector: "app-bill-detail",
  templateUrl: "./bill-detail.component.html",
  styleUrls: ["./bill-detail.component.scss"],
})
export class BillDetailComponent implements OnInit {
  bill: Bill | null = null
  loading = true
  error = ""

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private billService: BillService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get("id")
    if (id) {
      this.loadBill(+id)
    } else {
      this.error = "Bill ID not provided"
      this.loading = false
    }
  }

  loadBill(id: number): void {
    this.billService.getBillById(id).subscribe({
      next: (data) => {
        this.bill = data
        this.loading = false
      },
      error: (err) => {
        this.error = "Failed to load bill details. Please try again later."
        this.loading = false
        console.error(err)
      },
    })
  }

  downloadPdf(): void {
    if (this.bill?.id) {
      this.billService.downloadPdf(this.bill.id).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement("a")
          a.href = url
          a.download = `invoice_${this.bill?.id}.pdf`
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
  }

  editBill(): void {
    if (this.bill?.id) {
      this.router.navigate(["/bills", this.bill.id, "edit"])
    }
  }

  goBack(): void {
    this.router.navigate(["/bills"])
  }

  getStatusClass(): string {
    if (!this.bill) return ""

    switch (this.bill.status) {
      case "PAID":
        return "text-success"
      case "OVERDUE":
        return "text-warning"
      case "OVERDUE":
        return "text-danger"
      default:
        return ""
    }
  }
}
