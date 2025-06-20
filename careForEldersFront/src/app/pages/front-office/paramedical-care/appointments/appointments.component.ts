import { Component, OnInit} from "@angular/core"
import  { ActivatedRoute } from "@angular/router"
import  { ParamedicalService } from "../services/paramedical.service"
import  { AppointmentDTO, AppointmentRequest } from "../models/paramedical-professional.model"

@Component({
  selector: "app-appointments",
  templateUrl: "./appointments.component.html",
  styleUrls: ["./appointments.component.scss"],
})
export class AppointmentsComponent implements OnInit {
  appointments: AppointmentDTO[] = []
  elderId: string | null = null
  loading = false
  error: string | null = null
  showAppointmentForm = false
  selectedProfessionalId: string | null = null

  constructor(
    private route: ActivatedRoute,
    private paramedicalService: ParamedicalService,
  ) {}

  ngOnInit(): void {
    this.elderId = this.route.snapshot.paramMap.get("elderId")

    if (this.elderId) {
      this.loadAppointments(this.elderId)
    } else {
      this.showAppointmentForm = true
    }
  }

  onAppointmentSubmitted(request: AppointmentRequest) {
    this.loading = true
    this.paramedicalService.scheduleAppointment(request).subscribe({
      next: () => {
        this.loading = false
        this.loadAppointments(request.elderId)
      },
      error: (err) => {
        this.error = "Failed to save appointment. Please try again."
        this.loading = false
        console.error(err)
      },
    })
  }

  loadAppointments(elderId: string): void {
    this.loading = true
    this.error = null

    this.paramedicalService.getElderAppointments(elderId).subscribe({
      next: (data) => {
        this.appointments = data
        this.loading = false
      },
      error: (err) => {
        this.error = "Failed to load appointments. Please try again."
        this.loading = false
        console.error(err)
      },
    })
  }

  getStatusClass(status: string): string {
    switch (status.toUpperCase()) {
      case "SCHEDULED":
        return "bg-primary"
      case "COMPLETED":
        return "bg-success"
      case "CANCELLED":
        return "bg-danger"
      default:
        return "bg-secondary"
    }
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleString()
  }
}
