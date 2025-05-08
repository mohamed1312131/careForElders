import { Component,  OnInit } from "@angular/core"
import { FormBuilder, FormGroup, FormArray, Validators } from "@angular/forms"
import { Router, ActivatedRoute } from "@angular/router"
import  { MatSnackBar } from "@angular/material/snack-bar"
import  { Location } from "@angular/common"
import { PatientBillService } from "../patient-bill.service"

@Component({
  selector: "app-patient-bill-form",
  templateUrl: "./patient-bill-form.component.html",
  styleUrls: ["./patient-bill-form.component.scss"],
})
export class PatientBillFormComponent implements OnInit {
  billForm: FormGroup
  isLoading = false
  isEditMode = false
  isViewMode = false
  billId: string | null = null
  paymentStatuses = [
    { value: "PAID", viewValue: "Paid" },
    { value: "PENDING", viewValue: "Pending" },
    { value: "OVERDUE", viewValue: "Overdue" },
    { value: "CANCELLED", viewValue: "Cancelled" },
  ]

  constructor(
    private fb: FormBuilder,
    private patientBillService: PatientBillService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private location: Location,
  ) {
    this.billForm = this.createBillForm()
  }

  ngOnInit(): void {
    console.log("PatientBillFormComponent initialized")
    console.log("Current URL:", this.router.url)

    this.billId = this.route.snapshot.paramMap.get("id")
    this.isEditMode = this.router.url.includes("/edit/")
    this.isViewMode = this.router.url.includes("/view/")

    if ((this.isEditMode || this.isViewMode) && this.billId) {
      this.loadBillData(this.billId)
    } else {
      // Add default items for new bill
      this.addDefaultItems()
    }

    if (this.isViewMode) {
      this.billForm.disable()
    }

    // Calculate initial amounts for any existing items
    setTimeout(() => {
      for (let i = 0; i < this.items.length; i++) {
        this.calculateItemAmount(i)
      }
    }, 0)
  }

  createBillForm(): FormGroup {
    return this.fb.group({
      patientId: ["", Validators.required],
      patientName: ["", Validators.required],
      billDate: [new Date(), Validators.required],
      dueDate: [new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)],
      totalAmount: [{ value: 0, disabled: true }],
      paymentStatus: ["PENDING", Validators.required],
      notes: [""],
      items: this.fb.array([]),
    })
  }

  createItemFormGroup(): FormGroup {
    return this.fb.group({
      description: ["", Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitPrice: [0, [Validators.required, Validators.min(0)]],
      amount: [{ value: 0, disabled: true }],
    })
  }

  addDefaultItems(): void {
    // Clear existing items
    while (this.items.length) {
      this.items.removeAt(0)
    }

    // Add consultation item
    const consultationItem = this.createItemFormGroup()
    consultationItem.patchValue({
      description: "Consultation",
      quantity: 1,
      unitPrice: 80,
    })
    this.items.push(consultationItem)

    // Add radio item
    const radioItem = this.createItemFormGroup()
    radioItem.patchValue({
      description: "Radio",
      quantity: 1,
      unitPrice: 45,
    })
    this.items.push(radioItem)

    // Calculate amounts
    setTimeout(() => {
      for (let i = 0; i < this.items.length; i++) {
        this.calculateItemAmount(i)
      }
    }, 0)
  }

  get items(): FormArray {
    return this.billForm.get("items") as FormArray
  }

  addItem(): void {
    this.items.push(this.createItemFormGroup())
  }

  removeItem(index: number): void {
    if (this.items.length > 1) {
      this.items.removeAt(index)
      this.calculateTotals()
    } else {
      this.snackBar.open("Bill must have at least one item", "Close", {
        duration: 3000,
      })
    }
  }

  calculateItemAmount(index: number): void {
    const itemGroup = this.items.at(index) as FormGroup
    const quantity = itemGroup.get("quantity")?.value || 0
    const unitPrice = itemGroup.get("unitPrice")?.value || 0
    const amount = quantity * unitPrice

    itemGroup.get("amount")?.setValue(amount.toFixed(2))
    this.calculateTotals()
  }

  calculateTotals(): void {
    let total = 0
    for (let i = 0; i < this.items.length; i++) {
      const itemGroup = this.items.at(i) as FormGroup
      const quantity = itemGroup.get("quantity")?.value || 0
      const unitPrice = itemGroup.get("unitPrice")?.value || 0
      total += quantity * unitPrice
    }

    this.billForm.get("totalAmount")?.setValue(total.toFixed(2))
  }

  loadBillData(id: string): void {
    this.isLoading = true
    this.patientBillService.getBillById(id).subscribe({
      next: (bill) => {
        // Clear existing items
        while (this.items.length) {
          this.items.removeAt(0)
        }

        // Add items from the bill
        if (bill.items && bill.items.length > 0) {
          bill.items.forEach((item: any) => {
            const itemGroup = this.createItemFormGroup()
            itemGroup.patchValue({
              description: item.description,
              quantity: item.quantity,
              unitPrice: item.unitPrice,
              amount: item.amount,
            })
            this.items.push(itemGroup)
          })
        } else {
          // Add default items if none exist
          this.addDefaultItems()
        }

        // Set bill data
        this.billForm.patchValue({
          patientId: bill.patientId,
          patientName: bill.patientName,
          billDate: new Date(bill.billDate),
          dueDate: bill.dueDate ? new Date(bill.dueDate) : null,
          paymentStatus: bill.paymentStatus,
          notes: bill.notes,
          totalAmount: bill.totalAmount,
        })

        this.isLoading = false
      },
      error: (error) => {
        console.error("Error loading bill:", error)
        this.snackBar.open("Failed to load bill data", "Close", {
          duration: 5000,
          panelClass: ["error-snackbar"],
        })
        this.isLoading = false
        this.router.navigate(["/front-office/patient-bill"])
      },
    })
  }

  onSubmit(): void {
    if (this.billForm.invalid) {
      this.markFormGroupTouched(this.billForm)
      this.snackBar.open("Please fix the validation errors before submitting", "Close", {
        duration: 5000,
        panelClass: ["error-snackbar"],
      })
      return
    }

    this.isLoading = true

    // Prepare bill data
    const formValue = this.billForm.getRawValue()
    const billData = {
      patientId: formValue.patientId,
      patientName: formValue.patientName,
      billDate: this.formatDate(formValue.billDate),
      dueDate: formValue.dueDate ? this.formatDate(formValue.dueDate) : undefined,
      totalAmount: Number.parseFloat(formValue.totalAmount),
      paymentStatus: formValue.paymentStatus,
      notes: formValue.notes,
      items: formValue.items.map((item: any) => ({
        description: item.description,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        amount: Number.parseFloat(item.amount),
      })),
    }

    console.log("Submitting bill data:", billData)

    // If in edit mode, update the bill
    if (this.isEditMode && this.billId) {
      this.patientBillService.updateBill(this.billId, billData).subscribe({
        next: (response) => {
          console.log("Bill updated successfully:", response)
          this.snackBar.open("Bill updated successfully", "Close", {
            duration: 3000,
          })
          this.router.navigate(["/front-office/patient-bill"])
        },
        error: (error) => {
          console.error("Error updating bill:", error)
          let errorMessage = "Failed to update bill"

          if (error.error && error.error.errors) {
            errorMessage = error.error.errors.map((err: any) => `${err.field}: ${err.message}`).join(", ")
          } else if (error.error && error.error.message) {
            errorMessage = error.error.message
          } else if (error.message) {
            errorMessage = error.message
          }

          this.snackBar.open(errorMessage, "Close", {
            duration: 5000,
            panelClass: ["error-snackbar"],
          })
          this.isLoading = false
        },
        complete: () => {
          this.isLoading = false
        },
      })
    } else {
      // Create new bill
      this.patientBillService.createBill(billData).subscribe({
        next: (response) => {
          console.log("Bill created successfully:", response)
          this.snackBar.open("Bill created successfully", "Close", {
            duration: 3000,
          })
          this.router.navigate(["/front-office/patient-bill"])
        },
        error: (error) => {
          console.error("Error creating bill:", error)
          let errorMessage = "Failed to create bill"

          if (error.error && error.error.errors) {
            errorMessage = error.error.errors.map((err: any) => `${err.field}: ${err.message}`).join(", ")
          } else if (error.error && error.error.message) {
            errorMessage = error.error.message
          } else if (error.message) {
            errorMessage = error.message
          }

          this.snackBar.open(errorMessage, "Close", {
            duration: 5000,
            panelClass: ["error-snackbar"],
          })
          this.isLoading = false
        },
        complete: () => {
          this.isLoading = false
        },
      })
    }
  }

  goBack(): void {
    this.router.navigate(["/bill"])
  }

  private formatDate(date: Date): string {
    return date.toISOString().split("T")[0]
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach((control) => {
      control.markAsTouched()

      if ((control as any).controls) {
        if (control instanceof FormGroup) {
          this.markFormGroupTouched(control)
        } else if (control instanceof FormArray) {
          for (let i = 0; i < control.length; i++) {
            if (control.at(i) instanceof FormGroup) {
              this.markFormGroupTouched(control.at(i) as FormGroup)
            }
          }
        }
      }
    })
  }
}
