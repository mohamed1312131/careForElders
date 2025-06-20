import { Component, OnInit } from "@angular/core"
import  { ActivatedRoute, Router } from "@angular/router"
import  { ParamedicalService } from "../services/paramedical.service"
import  { ParamedicalProfessionalDTO, AppointmentRequest } from "../models/paramedical-professional.model"

@Component({
  selector: "app-professional-detail",
  templateUrl: "./professional-detail.component.html",
  styleUrls: ["./professional-detail.component.scss"],
})
export class ProfessionalDetailComponent implements OnInit {
  professional: ParamedicalProfessionalDTO | null = null
  loading = false
  error: string | null = null
  showAppointmentForm = false

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paramedicalService: ParamedicalService,
  ) {}

  ngOnInit(): void {
    this.loading = true
    const id = this.route.snapshot.paramMap.get("id")

    if (id) {
      this.paramedicalService.getProfessionalById(id).subscribe({
        next: (data) => {
          this.professional = data
          this.loading = false
        },
        error: (err) => {
          this.error = "Failed to load professional details. Please try again."
          this.loading = false
          console.error(err)
        },
      })
    } else {
      this.error = "Professional ID not provided"
      this.loading = false
    }
  }

  goBack(): void {
    this.router.navigate(["/paramedical/list"])
  }

  editProfessional(): void {
    if (this.professional?.id) {
      this.router.navigate(["/paramedical/edit", this.professional.id])
    }
  }

  toggleAppointmentForm(): void {
    this.showAppointmentForm = !this.showAppointmentForm
  }

  scheduleAppointment(appointmentRequest: AppointmentRequest): void {
    this.paramedicalService.scheduleAppointment(appointmentRequest).subscribe({
      next: () => {
        alert("Appointment scheduled successfully!")
        this.showAppointmentForm = false
      },
      error: (err) => {
        alert("Failed to schedule appointment. Please try again.")
        console.error(err)
      },
    })
  }
}
