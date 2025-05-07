import { Component, type OnInit } from "@angular/core"
import { PatientBillService } from "../services/patient-bill.service"
//import type { PatientBillService } from "../core/services/patient-bill.service"

@Component({
  selector: "app-api-test",
  templateUrl: "./api-test.component.html",
  styleUrls: ["./api-test.component.scss"],
})
export class ApiTestComponent implements OnInit {
  response: any = null
  testBillId = ""
  loading = false
  patientId = 101 // Default test patient ID

  // For creating/updating bills
  testBill: any = {
    billNumber: "",
    patientId: 101,
    patientName: "Test Patient",
    patientEmail: "test@example.com",
    patientPhone: "555-123-4567",
    billDate: new Date().toISOString().split("T")[0],
    dueDate: new Date(new Date().setDate(new Date().getDate() + 30)).toISOString().split("T")[0],
    totalAmount: 500.0,
    status: "PENDING",
    items: [
      {
        description: "General Consultation",
        quantity: 1,
        unitPrice: 150.0,
        serviceDate: new Date().toISOString().split("T")[0],
      },
      {
        description: "Blood Test",
        quantity: 1,
        unitPrice: 350.0,
        serviceDate: new Date().toISOString().split("T")[0],
      },
    ],
    notes: "Test bill created for API testing",
  }

  constructor(private billService: PatientBillService) {}

  ngOnInit(): void {
    // Generate a unique bill number
    this.testBill.billNumber = "TEST-" + new Date().getTime()
  }

  // Test getting all bills
  testGetAll(): void {
    this.loading = true
    this.response = null

    this.billService.getPatientBills().subscribe({
      next: (data) => {
        this.response = data
        console.log("Get All Success:", data)
        if (data && data.length > 0) {
          this.testBillId = data[0].id
        }
        this.loading = false
      },
      error: (err) => {
        this.response = err
        console.error("Get All Error:", err)
        this.loading = false
      },
    })
  }

  // Test creating a new bill
  testCreate(): void {
    this.loading = true
    this.response = null

    // Update the bill number to ensure uniqueness
    this.testBill.billNumber = "TEST-" + new Date().getTime()

    this.billService.createPatientBill(this.testBill).subscribe({
      next: (data) => {
        this.response = data
        console.log("Create Success:", data)
        if (data && data.id) {
          this.testBillId = data.id
        }
        this.loading = false
      },
      error: (err) => {
        this.response = err
        console.error("Create Error:", err)
        this.loading = false
      },
    })
  }

  // Test getting a bill by ID
  testGetById(): void {
    if (!this.testBillId) {
      this.response = { error: "No bill ID available. Create a bill first or get all bills." }
      return
    }

    this.loading = true
    this.response = null

    this.billService.getPatientBill(this.testBillId).subscribe({
      next: (data) => {
        this.response = data
        console.log("Get By ID Success:", data)
        this.loading = false
      },
      error: (err) => {
        this.response = err
        console.error("Get By ID Error:", err)
        this.loading = false
      },
    })
  }

  // Test getting bills by patient ID
  testGetByPatientId(): void {
    this.loading = true
    this.response = null

    this.billService.getPatientBillsByPatientId(this.patientId).subscribe({
      next: (data) => {
        this.response = data
        console.log("Get By Patient ID Success:", data)
        if (data && data.length > 0) {
          this.testBillId = data[0].id
        }
        this.loading = false
      },
      error: (err) => {
        this.response = err
        console.error("Get By Patient ID Error:", err)
        this.loading = false
      },
    })
  }

  // Test updating a bill
  testUpdate(): void {
    if (!this.testBillId) {
      this.response = { error: "No bill ID available. Create a bill first or get all bills." }
      return
    }

    this.loading = true
    this.response = null

    // First get the bill
    this.billService.getPatientBill(this.testBillId).subscribe({
      next: (bill) => {
        // Update some fields
        bill.notes = "Updated at " + new Date().toISOString()
        bill.status = "UPDATED"

        // Send the update
        this.billService.updatePatientBill(bill).subscribe({
          next: (updatedBill) => {
            this.response = updatedBill
            console.log("Update Success:", updatedBill)
            this.loading = false
          },
          error: (err) => {
            this.response = err
            console.error("Update Error:", err)
            this.loading = false
          },
        })
      },
      error: (err) => {
        this.response = err
        console.error("Get for Update Error:", err)
        this.loading = false
      },
    })
  }

  // Test deleting a bill
  testDelete(): void {
    if (!this.testBillId) {
      this.response = { error: "No bill ID available. Create a bill first or get all bills." }
      return
    }

    this.loading = true
    this.response = null

    this.billService.deletePatientBill(this.testBillId).subscribe({
      next: (data) => {
        this.response = { success: true, message: `Bill ${this.testBillId} deleted successfully` }
        console.log("Delete Success:", data)
        this.testBillId = ""
        this.loading = false
      },
      error: (err) => {
        this.response = err
        console.error("Delete Error:", err)
        this.loading = false
      },
    })
  }

  // Test generating a PDF
  testGeneratePdf(): void {
    if (!this.testBillId) {
      this.response = { error: "No bill ID available. Create a bill first or get all bills." }
      return
    }

    this.loading = true
    this.response = null

    this.billService.generatePdf(this.testBillId).subscribe({
      next: (blob) => {
        this.response = { success: true, message: "PDF generated successfully", size: blob.size + " bytes" }
        console.log("PDF Success:", blob)

        // Open the PDF in a new tab
        const url = window.URL.createObjectURL(blob)
        window.open(url, "_blank")
        this.loading = false
      },
      error: (err) => {
        this.response = err
        console.error("PDF Error:", err)
        this.loading = false
      },
    })
  }
}
