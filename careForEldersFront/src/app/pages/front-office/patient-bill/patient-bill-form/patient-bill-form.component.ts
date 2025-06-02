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
  originalBillData: any = null;
  
  paymentStatuses = [
    { value: "PAID", viewValue: "Paid" },
    { value: "PENDING", viewValue: "Pending" },
    { value: "PARTIALLY_PAID", viewValue: "Partially Paid" },
    { value: "CANCELLED", viewValue: "Cancelled" },
  ];
  
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
      // For new bills, set default items based on service type
      this.handleServiceTypeChange();
    }

    // Apply form restrictions based on mode
    this.applyFormRestrictions();

    // Listen for service type changes in create mode
    if (!this.isEditMode && !this.isViewMode) {
      this.billForm.get('serviceType')?.valueChanges.subscribe(serviceType => {
        this.handleServiceTypeChange(serviceType);
      });
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
      id: [""],
      billNumber: [""],
      patientId: ["", Validators.required],
      patientName: ["", Validators.required],
      patientEmail: [""],
      patientPhone: [""],
      serviceType: ["DOCTOR_CARE", Validators.required],
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
      id: [null],
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

  // New method to handle service type changes
  handleServiceTypeChange(serviceType?: string): void {
    const currentServiceType = serviceType || this.billForm.get('serviceType')?.value;
    
    // Clear existing items
    while (this.items.length) {
      this.items.removeAt(0);
    }

    // Add default items based on service type
    if (currentServiceType === 'DOCTOR_CARE') {
      this.addDoctorCareDefaultItem();
    }
    // For PARA_MEDICAL_SERVICES and SUBSCRIPTION, don't add any default items
    
    console.log(`Service type changed to: ${currentServiceType}`);
    console.log(`Items count after change: ${this.items.length}`);
  }

  // New method to add default item for Doctor Care
  addDoctorCareDefaultItem(): void {
    const consultationItem = this.createItemFormGroup();
    consultationItem.patchValue({
      serviceType: "DOCTOR_CARE",
      description: "Medical Consultation Services",
      serviceCode: "MED-CONSULT-01",
      category: "Doctor Care",
      quantity: 1,
      unitPrice: 80,
      unit: "Session",
      serviceDate: new Date(),
    });
    this.items.push(consultationItem);

    setTimeout(() => {
      this.calculateItemAmount(0);
    }, 100);
  }

  // Updated method - now conditional based on service type
  addDefaultItems(): void {
    const serviceType = this.billForm.get('serviceType')?.value || 'DOCTOR_CARE';
    this.handleServiceTypeChange(serviceType);
  }

  // New method to apply form restrictions based on mode
  applyFormRestrictions(): void {
    if (this.isViewMode) {
      // In view mode, disable all fields
      this.billForm.disable();
    } else if (this.isEditMode) {
      // In edit mode, disable all fields except items
      this.disableAllFieldsExceptItems();
    }
  }

  // New method to disable all fields except items
  disableAllFieldsExceptItems(): void {
    // Disable all main form fields
    Object.keys(this.billForm.controls).forEach(key => {
      if (key !== 'items') {
        this.billForm.get(key)?.disable();
      }
    });

    // Keep items enabled for editing
    this.items.enable();
  }

  get items(): FormArray {
    return this.billForm.get("items") as FormArray;
  }

  addItem(): void {
    // Only allow adding items if not in view mode
    if (this.isViewMode) {
      return;
    }

    console.log("Adding new item...");
    const serviceType = this.billForm.get("serviceType")?.value || "DOCTOR_CARE";
    const newItem = this.createItemFormGroup();
    
    // Set default values based on service type
    let defaultDescription = "";
    let defaultServiceCode = this.generateServiceCode();
    let defaultUnitPrice = 0;

    switch (serviceType) {
      case "DOCTOR_CARE":
        defaultDescription = "Medical Consultation Services";
        defaultServiceCode = "MED-CONSULT-" + this.generateServiceCode().split('-')[1];
        defaultUnitPrice = 80;
        break;
      case "PARA_MEDICAL_SERVICES":
        defaultDescription = "";
        defaultUnitPrice = 0;
        break;
      case "SUBSCRIPTION":
        defaultDescription = "";
        defaultUnitPrice = 0;
        break;
      default:
        defaultDescription = "";
        defaultUnitPrice = 0;
    }
    
    newItem.patchValue({
      id: null,
      serviceType: serviceType,
      description: defaultDescription,
      serviceCode: defaultServiceCode,
      category: this.getDisplayNameForServiceType(serviceType),
      quantity: 1,
      unitPrice: defaultUnitPrice,
      unit: "Session",
      serviceDate: new Date(),
    });

    this.items.push(newItem);
    console.log("New item added. Total items:", this.items.length);
    
    setTimeout(() => {
      const newIndex = this.items.length - 1;
      this.calculateItemAmount(newIndex);
      
      // Focus on the new item's description field if it's empty
      if (!defaultDescription) {
        const descriptionInput = document.querySelector(`input[formControlName="description"]:nth-of-type(${newIndex + 1})`);
        if (descriptionInput) {
          (descriptionInput as HTMLElement).focus();
        }
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
    // Only allow removing items if not in view mode
    if (this.isViewMode) {
      return;
    }

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

    // Update total amount
    this.billForm.get("totalAmount")?.setValue(total, { emitEvent: false });
    
    // Calculate balance amount (total - paid)
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
        this.originalBillData = { ...bill };
        
        while (this.items.length) {
          this.items.removeAt(0);
        }

        if (bill.items && bill.items.length > 0) {
          bill.items.forEach((item: any) => {
            const itemGroup = this.createItemFormGroup();
            itemGroup.patchValue({
              id: item.id,
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
          // If no items exist, add default items based on service type
          this.handleServiceTypeChange(bill.serviceType);
        }

        this.billForm.patchValue({
          id: bill.id,
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

        // Apply form restrictions after loading data
        this.applyFormRestrictions();

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
    // Don't allow submission in view mode
    if (this.isViewMode) {
      return;
    }

    if (this.billForm.invalid) {
      this.markFormGroupTouched(this.billForm);
      this.snackBar.open("Please fix the validation errors before submitting", "Close", {
        duration: 5000,
        panelClass: ["error-snackbar"],
      });
      return;
    }

    this.isLoading = true;

    const formValue = this.billForm.getRawValue();
    
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
        id: item.id,
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

    try {
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

  // Helper method to check if form should be editable
  isFormEditable(): boolean {
    return !this.isViewMode;
  }

  // Helper method to check if items should be editable
  areItemsEditable(): boolean {
    return !this.isViewMode;
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