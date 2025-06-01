import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormArray, Validators } from "@angular/forms";
import { Router, ActivatedRoute } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Location } from "@angular/common";
import { PatientBillService } from "../patient-bill.service";

@Component({
  selector: "app-patient-bill-form",
  templateUrl: "./patient-bill-form.component.html",
  styleUrls: ["./patient-bill-form.component.scss"],
})
export class PatientBillFormComponent implements OnInit {
  billForm: FormGroup;
  isLoading = false;
  isEditMode = false;
  isViewMode = false;
  billId: string | null = null;
  originalBillData: any = null; // Store original bill data
  
  // Updated to match backend status values
  paymentStatuses = [
    { value: "PAID", viewValue: "Paid" },
    { value: "PENDING", viewValue: "Pending" },
    { value: "PARTIALLY_PAID", viewValue: "Partially Paid" },
    { value: "CANCELLED", viewValue: "Cancelled" },
  ];
  
  // Service types from BillItem.ServiceType enum
  serviceTypes = [
    { value: "DOCTOR_CARE", viewValue: "Doctor Care" },
    { value: "PARA_MEDICAL_SERVICES", viewValue: "Para Medical Services" },
    { value: "SUBSCRIPTION", viewValue: "Subscription" },
  ];

  constructor(
    private fb: FormBuilder,
    private patientBillService: PatientBillService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private location: Location,
  ) {
    this.billForm = this.createBillForm();
  }

  ngOnInit(): void {
    console.log("PatientBillFormComponent initialized");
    console.log("Current URL:", this.router.url);

    this.billId = this.route.snapshot.paramMap.get("id");
    this.isEditMode = this.router.url.includes("/edit/");
    this.isViewMode = this.router.url.includes("/view/");

    console.log("Bill ID:", this.billId);
    console.log("Edit Mode:", this.isEditMode);
    console.log("View Mode:", this.isViewMode);

    if ((this.isEditMode || this.isViewMode) && this.billId) {
      this.loadBillData(this.billId);
    } else {
      // Add default items for new bill
      this.addDefaultItems();
    }

    if (this.isViewMode) {
      this.billForm.disable();
    }

    // Calculate initial amounts for any existing items
    setTimeout(() => {
      for (let i = 0; i < this.items.length; i++) {
        this.calculateItemAmount(i);
      }
    }, 100);
  }

  createBillForm(): FormGroup {
    return this.fb.group({
      id: [""], // Add ID field to track the bill
      billNumber: [""],
      patientId: ["", Validators.required],
      patientName: ["", Validators.required],
      patientEmail: [""],
      patientPhone: [""],
      serviceType: ["DOCTOR_CARE", Validators.required], // Default to DOCTOR_CARE
      billDate: [new Date(), Validators.required],
      dueDate: [new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)],
      totalAmount: [{ value: 0, disabled: true }],
      paidAmount: [{ value: 0, disabled: true }],
      balanceAmount: [{ value: 0, disabled: true }],
      status: ["PENDING", Validators.required],
      notes: [""],
      items: this.fb.array([]),
    });
  }

  createItemFormGroup(): FormGroup {
    return this.fb.group({
      id: [null], // For existing items, null for new items
      serviceType: ["", Validators.required],
      description: ["", Validators.required],
      serviceCode: [""],
      category: [""],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitPrice: [0, [Validators.required, Validators.min(0)]],
      amount: [{ value: 0, disabled: true }],
      unit: ["Session", Validators.required],
      serviceDate: [new Date(), Validators.required],
    });
  }

  addDefaultItems(): void {
    // Clear existing items
    while (this.items.length) {
      this.items.removeAt(0);
    }

    // Add consultation item
    const consultationItem = this.createItemFormGroup();
    consultationItem.patchValue({
      serviceType: "DOCTOR_CARE",
      description: "Doctor Consultation",
      serviceCode: "CONSULT-01",
      category: "Doctor Care",
      quantity: 1,
      unitPrice: 80,
      unit: "Session",
      serviceDate: new Date(),
    });
    this.items.push(consultationItem);

    // Calculate amounts
    setTimeout(() => {
      this.calculateItemAmount(0);
    }, 100);
  }

  get items(): FormArray {
    return this.billForm.get("items") as FormArray;
  }

  addItem(): void {
    console.log("Adding new item...");
    const serviceType = this.billForm.get("serviceType")?.value || "DOCTOR_CARE";
    const newItem = this.createItemFormGroup();
    
    // Set default values for new item
    newItem.patchValue({
      id: null, // Explicitly set to null for new items
      serviceType: serviceType,
      description: "",
      serviceCode: this.generateServiceCode(),
      category: this.getDisplayNameForServiceType(serviceType),
      quantity: 1,
      unitPrice: 0,
      unit: "Session",
      serviceDate: new Date(),
    });

    this.items.push(newItem);
    console.log("New item added. Total items:", this.items.length);
    
    // Focus on the new item's description field
    setTimeout(() => {
      const newIndex = this.items.length - 1;
      const descriptionInput = document.querySelector(`input[formControlName="description"]:nth-of-type(${newIndex + 1})`);
      if (descriptionInput) {
        (descriptionInput as HTMLElement).focus();
      }
    }, 100);
  }

  generateServiceCode(): string {
    const timestamp = Date.now().toString().slice(-6);
    return `SVC-${timestamp}`;
  }

  getDisplayNameForServiceType(serviceType: string): string {
    const type = this.serviceTypes.find(t => t.value === serviceType);
    return type ? type.viewValue : serviceType;
  }

  removeItem(index: number): void {
    if (this.items.length > 1) {
      console.log(`Removing item at index ${index}`);
      this.items.removeAt(index);
      this.calculateTotals();
      console.log("Item removed. Remaining items:", this.items.length);
    } else {
      this.snackBar.open("Bill must have at least one item", "Close", {
        duration: 3000,
      });
    }
  }

  calculateItemAmount(index: number): void {
    const itemGroup = this.items.at(index) as FormGroup;
    if (!itemGroup) return;

    const quantity = Number(itemGroup.get("quantity")?.value) || 0;
    const unitPrice = Number(itemGroup.get("unitPrice")?.value) || 0;
    const amount = quantity * unitPrice;

    itemGroup.get("amount")?.setValue(amount, { emitEvent: false });
    this.calculateTotals();
  }

  calculateTotals(): void {
    let total = 0;
    for (let i = 0; i < this.items.length; i++) {
      const itemGroup = this.items.at(i) as FormGroup;
      const quantity = Number(itemGroup.get("quantity")?.value) || 0;
      const unitPrice = Number(itemGroup.get("unitPrice")?.value) || 0;
      total += quantity * unitPrice;
    }

    this.billForm.get("totalAmount")?.setValue(total, { emitEvent: false });
    
    // Calculate balance amount
    const paidAmount = Number(this.billForm.get("paidAmount")?.value) || 0;
    const balanceAmount = total - paidAmount;
    this.billForm.get("balanceAmount")?.setValue(balanceAmount, { emitEvent: false });
  }

  loadBillData(id: string): void {
    this.isLoading = true;
    console.log("Loading bill data for ID:", id);
    
    this.patientBillService.getBillById(id).subscribe({
      next: (bill) => {
        console.log("Loaded bill data:", bill);
        this.originalBillData = { ...bill }; // Store original data
        
        // Clear existing items
        while (this.items.length) {
          this.items.removeAt(0);
        }

        // Add items from the bill
        if (bill.items && bill.items.length > 0) {
          bill.items.forEach((item: any) => {
            const itemGroup = this.createItemFormGroup();
            itemGroup.patchValue({
              id: item.id, // Keep the original item ID
              serviceType: item.serviceType || "DOCTOR_CARE",
              description: item.description || "",
              serviceCode: item.serviceCode || this.generateServiceCode(),
              category: item.category || this.getDisplayNameForServiceType(item.serviceType),
              quantity: item.quantity || 1,
              unitPrice: item.unitPrice || 0,
              amount: item.amount || (item.quantity * item.unitPrice),
              unit: item.unit || "Session",
              serviceDate: item.serviceDate ? new Date(item.serviceDate) : new Date(),
            });
            this.items.push(itemGroup);
          });
        } else {
          // Add default items if none exist
          this.addDefaultItems();
        }

        // Set bill data - preserve the original ID
        this.billForm.patchValue({
          id: bill.id, // Preserve the original bill ID
          billNumber: bill.billNumber || "",
          patientId: bill.patientId || "",
          patientName: bill.patientName || "",
          patientEmail: bill.patientEmail || "",
          patientPhone: bill.patientPhone || "",
          serviceType: bill.serviceType || "DOCTOR_CARE",
          billDate: bill.billDate ? new Date(bill.billDate) : new Date(),
          dueDate: bill.dueDate ? new Date(bill.dueDate) : new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
          status: bill.status || "PENDING",
          paidAmount: bill.paidAmount || 0,
          balanceAmount: bill.balanceAmount || bill.totalAmount || 0,
          notes: bill.notes || "",
          totalAmount: bill.totalAmount || 0,
        });

        // Recalculate totals to ensure consistency
        setTimeout(() => {
          this.calculateTotals();
        }, 100);

        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error loading bill:", error);
        this.snackBar.open("Failed to load bill data", "Close", {
          duration: 5000,
          panelClass: ["error-snackbar"],
        });
        this.isLoading = false;
        this.router.navigate(["/user/userProfile/bill"]);
      },
    });
  }

  onSubmit(): void {
    if (this.billForm.invalid) {
      this.markFormGroupTouched(this.billForm);
      this.snackBar.open("Please fix the validation errors before submitting", "Close", {
        duration: 5000,
        panelClass: ["error-snackbar"],
      });
      return;
    }

    this.isLoading = true;

    // Prepare bill data
    const formValue = this.billForm.getRawValue();
    
    // For updates, preserve the original bill ID and number
    const billData = {
      id: this.isEditMode ? (this.billId || this.originalBillData?.id) : undefined,
      billNumber: this.isEditMode ? (this.originalBillData?.billNumber || formValue.billNumber) : formValue.billNumber,
      patientId: formValue.patientId,
      patientName: formValue.patientName,
      patientEmail: formValue.patientEmail || "",
      patientPhone: formValue.patientPhone || "",
      serviceType: formValue.serviceType,
      billDate: formValue.billDate,
      dueDate: formValue.dueDate,
      totalAmount: Number.parseFloat(formValue.totalAmount) || 0,
      paidAmount: Number.parseFloat(formValue.paidAmount) || 0,
      balanceAmount: Number.parseFloat(formValue.balanceAmount) || Number.parseFloat(formValue.totalAmount) || 0,
      status: formValue.status,
      notes: formValue.notes || "",
      items: formValue.items.map((item: any) => ({
        id: item.id, // Keep existing ID for updates, null for new items
        serviceType: item.serviceType,
        description: item.description,
        serviceCode: item.serviceCode || this.generateServiceCode(),
        category: item.category || this.getDisplayNameForServiceType(item.serviceType),
        quantity: Number(item.quantity) || 1,
        unitPrice: Number(item.unitPrice) || 0,
        amount: Number(item.amount) || (Number(item.quantity) * Number(item.unitPrice)),
        unit: item.unit || "Session",
        serviceDate: item.serviceDate,
      })),
    };

    console.log("Submitting bill data:", billData);
    console.log("Is Edit Mode:", this.isEditMode);
    console.log("Bill ID:", this.billId);

    try {
      // If in edit mode, update the bill
      if (this.isEditMode && this.billId) {
        this.patientBillService.updateBill(this.billId, billData).subscribe({
          next: (response) => {
            console.log("Bill updated successfully:", response);
            this.snackBar.open("Bill updated successfully", "Close", {
              duration: 3000,
            });
            this.router.navigate(["/user/userProfile/bill"]);
          },
          error: (error) => {
            this.handleSubmitError(error, "update");
          },
          complete: () => {
            this.isLoading = false;
          },
        });
      } else {
        // Create new bill
        this.patientBillService.createBill(billData).subscribe({
          next: (response) => {
            console.log("Bill created successfully:", response);
            this.snackBar.open("Bill created successfully", "Close", {
              duration: 3000,
            });
            this.router.navigate(["/user/userProfile/bill"]);
          },
          error: (error) => {
            this.handleSubmitError(error, "create");
          },
          complete: () => {
            this.isLoading = false;
          },
        });
      }
    } catch (error) {
      this.handleSubmitError(error, this.isEditMode ? "update" : "create");
    }
  }

  handleSubmitError(error: any, operation: string): void {
    console.error(`Error ${operation}ing bill:`, error);
    let errorMessage = `Failed to ${operation} bill`;

    if (error.error && error.error.errors) {
      errorMessage = error.error.errors.map((err: any) => `${err.field}: ${err.message}`).join(", ");
    } else if (error.error && error.error.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }

    this.snackBar.open(errorMessage, "Close", {
      duration: 5000,
      panelClass: ["error-snackbar"],
    });
    this.isLoading = false;
  }

  goBack(): void {
    this.router.navigate(["/user/userProfile/bill"]);
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach((control) => {
      control.markAsTouched();

      if ((control as any).controls) {
        if (control instanceof FormGroup) {
          this.markFormGroupTouched(control);
        } else if (control instanceof FormArray) {
          for (let i = 0; i < control.length; i++) {
            if (control.at(i) instanceof FormGroup) {
              this.markFormGroupTouched(control.at(i) as FormGroup);
            }
          }
        }
      }
    });
  }
}