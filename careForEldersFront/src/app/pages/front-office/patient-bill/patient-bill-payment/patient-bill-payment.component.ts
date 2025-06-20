import { Component,  OnInit } from "@angular/core"
import  { ActivatedRoute, Router } from "@angular/router"
import {  FormBuilder, FormGroup, Validators } from "@angular/forms"
import  { PatientBillService } from "../patient-bill.service"
import  { ToastrService } from "ngx-toastr"
import { catchError, finalize, tap } from "rxjs/operators"
import { of } from "rxjs"

@Component({
  selector: "app-patient-bill-payment",
  templateUrl: "./patient-bill-payment.component.html",
  styleUrls: ["./patient-bill-payment.component.scss"],
})
export class PatientBillPaymentComponent implements OnInit {
  billId = "" // Initialize with empty string to fix TypeScript error
  bill: any
  paymentForm!: FormGroup // Use definite assignment assertion
  paymentMethods = ["CASH", "ONLINE"]
  isLoading = false
  showCreditCardForm = false
  creditCardForm!: FormGroup // Use definite assignment assertion
  paymentId = "" // Initialize with empty string
  paymentSuccess = false
  paymentFailed = false
  paymentPending = false
  errorMessage = ""

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private billService: PatientBillService,
    private toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get("id")
    if (id) {
      this.billId = id
    } else {
      this.toastr.error("Bill ID not found in route parameters")
      this.router.navigate(["/front-office/patient-bill"])
      return
    }

    console.log(`Initializing payment component for bill ID: ${this.billId}`)
    this.initForms()
    this.loadBillDetails()
  }

  initForms(): void {
    console.log("Initializing payment forms")
    this.paymentForm = this.fb.group({
      amount: ["", [Validators.required, Validators.min(0.01)]],
      paymentMethod: ["CASH", Validators.required],
    })

    this.creditCardForm = this.fb.group({
      cardNumber: ["", [Validators.required, Validators.pattern("^[0-9]{16}$")]],
      cardholderName: ["", Validators.required],
      expiryDate: ["", [Validators.required, Validators.pattern("^(0[1-9]|1[0-2])/[0-9]{2}$")]],
      cvv: ["", [Validators.required, Validators.pattern("^[0-9]{3,4}$")]],
    })

    // Listen for payment method changes
    this.paymentForm.get("paymentMethod")?.valueChanges.subscribe((method) => {
      this.showCreditCardForm = method === "ONLINE"
      console.log(
        `Payment method changed to ${method}, credit card form ${this.showCreditCardForm ? "shown" : "hidden"}`,
      )
    })
  }

  loadBillDetails(): void {
    console.log(`Loading bill details for ID: ${this.billId}`)
    this.isLoading = true
    this.billService
      .getBillById(this.billId)
      .pipe(
        tap((data) => {
          console.log("Bill details loaded successfully:", data)
          this.bill = data
          // Set default amount to balance amount
          if (this.bill && this.bill.balanceAmount) {
            this.paymentForm.patchValue({
              amount: this.bill.balanceAmount,
            })
            console.log(`Default payment amount set to ${this.bill.balanceAmount}`)
          }
        }),
        catchError((error) => {
          console.error("Error loading bill details:", error)
          this.errorMessage = "Failed to load bill details. Please try again."
          this.toastr.error(this.errorMessage)
          return of(null)
        }),
        finalize(() => {
          this.isLoading = false
        }),
      )
      .subscribe()
  }

  onPaymentMethodChange(): void {
    const method = this.paymentForm.get("paymentMethod")?.value
    this.showCreditCardForm = method === "ONLINE"
    console.log(`Payment method changed to ${method}, credit card form ${this.showCreditCardForm ? "shown" : "hidden"}`)
  }

  processPayment(): void {
    if (this.paymentForm.invalid) {
      console.log("Payment form is invalid, marking fields as touched")
      this.markFormGroupTouched(this.paymentForm)
      return
    }

    if (this.showCreditCardForm && this.creditCardForm.invalid) {
      console.log("Credit card form is invalid, marking fields as touched")
      this.markFormGroupTouched(this.creditCardForm)
      return
    }

    this.isLoading = true
    const paymentData = {
      billId: this.billId,
      amount: this.paymentForm.get("amount")?.value,
      paymentMethod: this.paymentForm.get("paymentMethod")?.value,
    }

    console.log("Processing payment with data:", paymentData)

    this.billService
      .createPayment(this.billId, paymentData)
      .pipe(
        tap((response) => {
          console.log("Payment created successfully:", response)
          this.paymentId = response.paymentId

          if (paymentData.paymentMethod === "CASH") {
            console.log("Cash payment recorded, completing payment")
            this.paymentSuccess = true
            this.toastr.success("Cash payment recorded successfully")
          } else if (paymentData.paymentMethod === "ONLINE") {
            console.log("Online payment initiated, processing credit card")
            // For online payments, we need to process the credit card
            this.processOnlinePayment()
          }
        }),
        catchError((error) => {
          console.error("Error creating payment:", error)
          this.paymentFailed = true
          this.errorMessage = "Payment failed. Please try again."
          this.toastr.error(this.errorMessage)
          return of(null)
        }),
        finalize(() => {
          if (!this.paymentPending) {
            this.isLoading = false
          }
        }),
      )
      .subscribe()
  }

  processOnlinePayment(): void {
    if (!this.paymentId) {
      console.error("Payment ID not found")
      this.toastr.error("Payment ID not found")
      this.isLoading = false
      return
    }

    this.paymentPending = true
    console.log(`Processing online payment for payment ID: ${this.paymentId}`)

    const creditCardData = {
      ...this.creditCardForm.value,
      paymentId: this.paymentId,
      billId: this.billId,
    }

    console.log("Credit card data prepared (sensitive data masked):", {
      ...creditCardData,
      cardNumber: creditCardData.cardNumber ? "************" + creditCardData.cardNumber.slice(-4) : "",
      cvv: "***",
    })

    // Simulate online payment processing
    this.billService
      .simulateOnlinePayment(this.paymentId)
      .pipe(
        tap((response) => {
          console.log("Online payment simulation response:", response)
          if (response && response.status === "COMPLETED") {
            this.paymentSuccess = true
            this.paymentPending = false
            this.toastr.success("Payment processed successfully")
          } else {
            this.paymentFailed = true
            this.paymentPending = false
            this.errorMessage = "Payment failed. Please try again."
            this.toastr.error(this.errorMessage)
          }
        }),
        catchError((error) => {
          console.error("Error processing online payment:", error)
          this.paymentFailed = true
          this.paymentPending = false
          this.errorMessage = "Payment failed. Please try again."
          this.toastr.error(this.errorMessage)
          return of(null)
        }),
        finalize(() => {
          this.isLoading = false
        }),
      )
      .subscribe()
  }

  downloadReceipt(): void {
    if (!this.paymentId) {
      console.error("Payment ID not found")
      this.toastr.error("Payment ID not found")
      return
    }

    console.log(`Downloading receipt for payment ID: ${this.paymentId}`)
    try {
      this.billService.downloadPaymentReceipt(this.paymentId)
      this.toastr.success("Receipt download initiated")
    } catch (error) {
      console.error("Error downloading receipt:", error)
      this.toastr.error("Failed to download receipt")
    }
  }

  viewBillDetails(): void {
    console.log(`Navigating to bill details for ID: ${this.billId}`)
    this.router.navigate(["/front-office/patient-bill", this.billId])
  }

  backToBills(): void {
    console.log("Navigating back to bills list")
    this.router.navigate(["/front-office/patient-bill"])
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach((control) => {
      control.markAsTouched()
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control)
      }
    })
  }
}
