import { Component,  OnInit } from "@angular/core"
import {  FormBuilder,  FormGroup, Validators } from "@angular/forms"
import  { ActivatedRoute, Router } from "@angular/router"
import  { ParamedicalService } from "../services/paramedical.service"

@Component({
  selector: "app-professional-form",
  templateUrl: "./professional-form.component.html",
  styleUrls: ["./professional-form.component.scss"],
})
export class ProfessionalFormComponent implements OnInit {
  professionalForm: FormGroup
  isEditMode = false
  professionalId: string | null = null
  loading = false
  submitting = false
  error: string | null = null

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private paramedicalService: ParamedicalService,
  ) {
    this.professionalForm = this.createForm()
  }

  ngOnInit(): void {
    this.professionalId = this.route.snapshot.paramMap.get("id")
    this.isEditMode = !!this.professionalId

    if (this.isEditMode && this.professionalId) {
      this.loadProfessionalData(this.professionalId)
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      name: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      specialty: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      contactInfo: ["", [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      address: [""], // Only for create requests
    })
  }

  loadProfessionalData(id: string): void {
    this.loading = true
    this.paramedicalService.getProfessionalById(id).subscribe({
      next: (professional) => {
        this.professionalForm.patchValue({
          name: professional.name,
          specialty: professional.specialty,
          contactInfo: professional.contactInfo,
        })
        this.loading = false
      },
      error: (err) => {
        this.error = "Failed to load professional data. Please try again."
        this.loading = false
        console.error(err)
      },
    })
  }

  onSubmit(): void {
    if (this.professionalForm.invalid) {
      this.professionalForm.markAllAsTouched()
      return
    }

    this.submitting = true
    const formData = this.professionalForm.value

    if (this.isEditMode && this.professionalId) {
      // For update, only send fields that have values and remove address
      const updateData = {
        name: formData.name?.trim() || undefined,
        specialty: formData.specialty?.trim() || undefined,
        contactInfo: formData.contactInfo?.trim() || undefined,
      }




      this.paramedicalService.updateProfessional(this.professionalId, updateData).subscribe({
        next: () => {
          this.router.navigate(["user/userProfile/paramedical/detail", this.professionalId])
        },
        error: (err) => {
          this.error = "Failed to update professional. Please try again."
          this.submitting = false
          console.error(err)
        },
      })
    } else {
      // For create, include address
      this.paramedicalService.createProfessional(formData).subscribe({
        next: (response) => {
          this.router.navigate(["user/userProfile/paramedical/detail", response.id])
        },
        error: (err) => {
          this.error = "Failed to create professional. Please try again."
          this.submitting = false
          console.error(err)
        },
      })
    }
  }

  cancel(): void {
    if (this.isEditMode && this.professionalId) {
      this.router.navigate(["user/userProfile/paramedical/detail", this.professionalId])
    } else {
      this.router.navigate(["user/userProfile/paramedical/list"])
    }
  }
}
