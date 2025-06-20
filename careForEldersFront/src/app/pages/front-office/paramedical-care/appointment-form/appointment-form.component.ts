import { Component, Input, Output, EventEmitter } from "@angular/core"
import { FormBuilder, FormGroup, Validators } from "@angular/forms"
import { Router } from '@angular/router';
import { AppointmentRequest } from "../models/paramedical-professional.model"

@Component({
  selector: "app-appointment-form",
  templateUrl: "./appointment-form.component.html",
  styleUrls: ["./appointment-form.component.scss"],
})
export class AppointmentFormComponent {
  @Input() professionalId!: string
  @Output() appointmentSubmitted = new EventEmitter<AppointmentRequest>()

  appointmentForm: FormGroup
  submitting = false

  constructor(private fb: FormBuilder, private router: Router) {
    this.appointmentForm = this.createForm()
  }

  createForm(): FormGroup {
    return this.fb.group({
      elderId: ["", [Validators.required]],
      appointmentTime: ["", [Validators.required]],
      location: [""],
      notes: [""],
    })
  }

  onSubmit(): void {
    if (this.appointmentForm.invalid) {
      this.appointmentForm.markAllAsTouched()
      return
    }

    this.submitting = true
    const formData = this.appointmentForm.value

    const appointmentRequest: AppointmentRequest = {
      elderId: formData.elderId,
      professionalId: this.professionalId,
      appointmentTime: new Date(formData.appointmentTime),
      location: formData.location || undefined,
      notes: formData.notes || undefined,
    }

    this.appointmentSubmitted.emit(appointmentRequest)
    this.submitting = false
    this.appointmentForm.reset()
    // Redirect to appointments list for this elder
    this.router.navigate(['/paramedical-care/appointments', formData.elderId]);
  }
}
