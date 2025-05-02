import { Component, type OnInit } from "@angular/core"
import { type FormBuilder, FormGroup, FormArray, Validators, type AbstractControl } from "@angular/forms"
import type { ActivatedRoute, Router } from "@angular/router"
import type { Bill, BillItem } from "../../../models/bill.model"
import type { BillService } from "../../../services/bill.service"
import { BillValidators } from "../../../shared/validators/bill-validators"
import type { MatSnackBar } from "@angular/material/snack-bar"
import { finalize } from "rxjs/operators"

@Component({
  selector: "app-bill-form",
  templateUrl: "./bill-form.component.html",
  styleUrls: ["./bill-form.component.scss"],
})
export class BillFormComponent implements OnInit {
  billForm!: FormGroup
  isEditMode = false
  billId?: number
  loading = false
  submitting = false
  error = ""
  validationMessages: { [key: string]: { [key: string]: string } } = {
    patientId: {
      required: "Patient ID is required",
      pattern: "Patient ID must contain only numbers",
      invalidPatientIdFormat: "Patient ID must be in format XX000000 (2 letters followed by 6 digits)",
    },
    patientName: {
      required: "Patient name is required",
      minlength: "Patient name must be at least 3 characters",
      maxlength: "Patient name cannot exceed 100 characters",
    },
    billDate: {
      required: "Bill date is required",
    },
    dueDate: {
      required: "Due date is required",
    },
    status: {
      required: "Status is required",
    },
    items: {
      noItems: "At least one item is required",
    },
    description: {
      maxlength: "Description cannot exceed 500 characters",
    },
  }

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private billService: BillService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.initForm()

    const id = this.route.snapshot.paramMap.get("id")
    if (id && id !== "new") {
      this.isEditMode = true
      this.billId = +id
      this.loadBill(this.billId)
    }
  }

  initForm(): void {
    this.billForm = this.fb.group(
      {
        patientId: [
          "",
          [
            Validators.required,
            Validators.pattern("^[0-9]+$"),
            // Uncomment if you want to use the custom format validator
            // BillValidators.patientIdFormat()
          ],
        ],
        patientName: ["", [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
        billDate: [this.formatDate(new Date()), [Validators.required]],
        dueDate: [this.formatDate(this.addDays(new Date(), 30)), [Validators.required]],
        status: ["UNPAID", [Validators.required]],
        description: ["", [Validators.maxLength(500)]],
        items: this.fb.array([this.createItemFormGroup()], [Validators.required]),
        totalAmount: [{ value: 0, disabled: true }],
      },
      {
        validators: [BillValidators.dueDateAfterBillDate(), BillValidators.hasItems()],
      },
    )

    // Listen for changes to recalculate totals
    this.billForm.get("items")?.valueChanges.subscribe(() => {
      this.calculateTotals()
    })
  }

  createItemFormGroup(item?: BillItem): FormGroup {
    return this.fb.group({
      description: [item?.description || "", [Validators.required, Validators.maxLength(200)]],
      quantity: [item?.quantity || 1, [Validators.required, Validators.min(1), Validators.max(9999)]],
      unitPrice: [item?.unitPrice || 0, [Validators.required, Validators.min(0), Validators.max(999999.99)]],
      amount: [{ value: item?.amount || 0, disabled: true }],
    })
  }

  get itemsFormArray(): FormArray {
    return this.billForm.get("items") as FormArray
  }

  addItem(): void {
    this.itemsFormArray.push(this.createItemFormGroup())
  }

  removeItem(index: number): void {
    if (this.itemsFormArray.length > 1) {
      this.itemsFormArray.removeAt(index)
      this.calculateTotals()
    } else {
      this.snackBar.open("At least one item is required", "Close", {
        duration: 3000,
        panelClass: "error-snackbar",
      })
    }
  }

  loadBill(id: number): void {
    this.loading = true
    this.billService.getBillById(id).subscribe({
      next: (bill) => {
        this.patchFormValues(bill)
        this.loading = false
      },
      error: (err) => {
        this.handleError(err, "Failed to load bill. Please try again later.")
        this.loading = false
      },
    })
  }

  patchFormValues(bill: Bill): void {
    // Clear existing items
    while (this.itemsFormArray.length) {
      this.itemsFormArray.removeAt(0)
    }

    // Add items from bill
    if (bill.items && bill.items.length > 0) {
      bill.items.forEach((item) => {
        this.itemsFormArray.push(this.createItemFormGroup(item))
      })
    } else {
      this.itemsFormArray.push(this.createItemFormGroup())
    }

    // Patch other values
    this.billForm.patchValue({
      patientId: bill.patientId,
      patientName: bill.patientName,
      billDate: this.formatDate(new Date(bill.billDate)),
      dueDate: this.formatDate(new Date(bill.dueDate)),
      status: bill.status,
      description: bill.description || "",
      totalAmount: bill.totalAmount,
    })

    this.calculateTotals()
  }

  onItemChange(index: number): void {
    const itemGroup = this.itemsFormArray.at(index) as FormGroup
    const quantity = itemGroup.get("quantity")?.value || 0
    const unitPrice = itemGroup.get("unitPrice")?.value || 0
    const amount = quantity * unitPrice

    itemGroup.get("amount")?.setValue(amount)
    this.calculateTotals()
  }

  calculateTotals(): void {
    let total = 0
    for (let i = 0; i < this.itemsFormArray.length; i++) {
      const itemGroup = this.itemsFormArray.at(i) as FormGroup
      const quantity = itemGroup.get("quantity")?.value || 0
      const unitPrice = itemGroup.get("unitPrice")?.value || 0
      const amount = quantity * unitPrice

      itemGroup.get("amount")?.setValue(amount)
      total += amount
    }

    this.billForm.get("totalAmount")?.setValue(total)
  }

  onSubmit(): void {
    if (this.billForm.invalid) {
      this.markFormGroupTouched(this.billForm)
      this.showValidationErrors()
      return
    }

    const formValue = this.billForm.getRawValue()
    const bill: Bill = {
      patientId: +formValue.patientId,
      patientName: formValue.patientName,
      billDate: formValue.billDate,
      dueDate: formValue.dueDate,
      status: formValue.status,
      description: formValue.description,
      totalAmount: this.calculateTotalAmount(),
      items: formValue.items.map((item: any) => ({
        description: item.description,
        quantity: +item.quantity,
        unitPrice: +item.unitPrice,
        amount: +item.amount,
      })),
    }

    this.submitting = true

    if (this.isEditMode && this.billId) {
      bill.id = this.billId
      this.billService
        .updateBill(this.billId, bill)
        .pipe(finalize(() => (this.submitting = false)))
        .subscribe({
          next: () => {
            this.snackBar.open("Bill updated successfully", "Close", { duration: 3000 })
            this.router.navigate(["/billing/bills", this.billId])
          },
          error: (err) => {
            this.handleError(err, "Failed to update bill. Please try again later.")
          },
        })
    } else {
      this.billService
        .createBill(bill)
        .pipe(finalize(() => (this.submitting = false)))
        .subscribe({
          next: (newBill) => {
            this.snackBar.open("Bill created successfully", "Close", { duration: 3000 })
            this.router.navigate(["/billing/bills", newBill.id])
          },
          error: (err) => {
            this.handleError(err, "Failed to create bill. Please try again later.")
          },
        })
    }
  }

  handleError(err: any, defaultMessage: string): void {
    console.error(err)

    // Check if it's a validation error from the server
    if (err.error && err.error.errors) {
      const serverErrors = err.error.errors
      this.processServerErrors(serverErrors)
    } else {
      this.error = defaultMessage
    }
  }

  processServerErrors(errors: any[]): void {
    // Reset previous errors
    this.error = ""

    // Process each error and apply to the form
    errors.forEach((error) => {
      const control = this.findControl(error.field)
      if (control) {
        control.setErrors({ serverError: error.message })
      } else {
        // If control not found, show general error
        this.error += `${error.message}. `
      }
    })

    if (this.error) {
      this.snackBar.open(this.error, "Close", {
        duration: 5000,
        panelClass: "error-snackbar",
      })
    }
  }

  findControl(path: string): AbstractControl | null {
    // Handle nested paths like 'items[0].quantity'
    if (path.includes("[") && path.includes("]")) {
      const match = path.match(/(.+)\[(\d+)\]\.(.+)/)
      if (match) {
        const [_, arrayName, index, fieldName] = match
        const formArray = this.billForm.get(arrayName) as FormArray
        if (formArray && +index < formArray.length) {
          return (formArray.at(+index) as FormGroup).get(fieldName)
        }
      }
      return null
    }

    return this.billForm.get(path)
  }

  showValidationErrors(): void {
    const formErrors: string[] = []

    // Check for form-level errors
    if (this.billForm.errors) {
      if (this.billForm.errors["dueDateBeforeBillDate"]) {
        formErrors.push("Due date must be after bill date")
      }
      if (this.billForm.errors["noItems"]) {
        formErrors.push("At least one item is required")
      }
    }

    // Check for control-level errors
    Object.keys(this.billForm.controls).forEach((key) => {
      const control = this.billForm.get(key)
      if (control && control.invalid && (control.dirty || control.touched)) {
        Object.keys(control.errors || {}).forEach((errorKey) => {
          if (this.validationMessages[key] && this.validationMessages[key][errorKey]) {
            formErrors.push(this.validationMessages[key][errorKey])
          }
        })
      }
    })

    if (formErrors.length > 0) {
      this.snackBar.open(`Please fix the following errors: ${formErrors.join(", ")}`, "Close", {
        duration: 5000,
        panelClass: "error-snackbar",
      })
    }
  }

  calculateTotalAmount(): number {
    let total = 0
    for (const item of this.itemsFormArray.controls) {
      const quantity = item.get("quantity")?.value || 0
      const unitPrice = item.get("unitPrice")?.value || 0
      total += quantity * unitPrice
    }
    return total
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach((control) => {
      control.markAsTouched()
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control)
      } else if (control instanceof FormArray) {
        control.controls.forEach((c) => {
          if (c instanceof FormGroup) {
            this.markFormGroupTouched(c)
          } else {
            c.markAsTouched()
          }
        })
      }
    })
  }

  getErrorMessage(controlName: string): string {
    const control = this.billForm.get(controlName)
    if (!control || !control.errors || (!control.dirty && !control.touched)) {
      return ""
    }

    const errors = control.errors
    const messages = this.validationMessages[controlName]

    for (const errorKey in errors) {
      if (messages && messages[errorKey]) {
        return messages[errorKey]
      }
    }

    // If we have a server error
    if (errors["serverError"]) {
      return errors["serverError"]
    }

    return "Invalid value"
  }

  getItemErrorMessage(index: number, controlName: string): string {
    const itemGroup = this.itemsFormArray.at(index) as FormGroup
    const control = itemGroup.get(controlName)

    if (!control || !control.errors || (!control.dirty && !control.touched)) {
      return ""
    }

    if (control.errors["required"]) {
      return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} is required`
    }

    if (control.errors["min"]) {
      return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} must be at least ${control.errors["min"].min}`
    }

    if (control.errors["max"]) {
      return `${controlName.charAt(0).toUpperCase() + controlName.slice(1)} cannot exceed ${control.errors["max"].max}`
    }

    if (control.errors["serverError"]) {
      return control.errors["serverError"]
    }

    return "Invalid value"
  }

  hasFormError(errorName: string): boolean {
    return this.billForm.errors !== null && errorName in this.billForm.errors
  }

  cancel(): void {
    if (this.isEditMode && this.billId) {
      this.router.navigate(["/billing/bills", this.billId])
    } else {
      this.router.navigate(["/billing/bills"])
    }
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear()
    const month = (date.getMonth() + 1).toString().padStart(2, "0")
    const day = date.getDate().toString().padStart(2, "0")
    return `${year}-${month}-${day}`
  }

  private addDays(date: Date, days: number): Date {
    const result = new Date(date)
    result.setDate(result.getDate() + days)
    return result
  }
}
