import { Component } from "@angular/core"
import {  FormBuilder,  FormGroup, Validators } from "@angular/forms"
import  { Router } from "@angular/router"
import  { ParamedicalService } from "../services/paramedical.service"
import  { ParamedicalProfessionalDTO } from "../models/paramedical-professional.model"

@Component({
  selector: "app-nearby-professionals",
  templateUrl: "./nearby-professionals.component.html",
  styleUrls: ["./nearby-professionals.component.scss"],
})
export class NearbyProfessionalsComponent {
  searchForm: FormGroup
  professionals: ParamedicalProfessionalDTO[] = []
  loading = false
  searched = false
  error: string | null = null

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private paramedicalService: ParamedicalService,
  ) {
    this.searchForm = this.createForm()
  }

  createForm(): FormGroup {
    return this.fb.group({
      location: ["", [Validators.required]],
      specialty: [""],
      distanceKm: [10, [Validators.required, Validators.min(1), Validators.max(100)]],
    })
  }

  onSubmit(): void {
    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched()
      return
    }

    this.loading = true
    this.error = null
    const { location, specialty, distanceKm } = this.searchForm.value

    this.paramedicalService.findNearbyProfessionals(location, specialty, distanceKm).subscribe({
      next: (data) => {
        this.professionals = data
        this.loading = false
        this.searched = true
      },
      error: (err) => {
        this.error = "Failed to find nearby professionals. Please try again."
        this.loading = false
        console.error(err)
      },
    })
  }

  viewDetails(id: string): void {
    this.router.navigate(["/paramedical/detail", id])
  }

  goBack(): void {
    this.router.navigate(["user/userProfile/paramedical/list"])
  }
}
