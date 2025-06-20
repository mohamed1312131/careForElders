import { Component, OnInit } from "@angular/core"
import  { Router } from "@angular/router"
import  { ParamedicalService } from "../services/paramedical.service"
import  { ParamedicalProfessionalDTO } from "../models/paramedical-professional.model"

@Component({
  selector: "app-professional-list",
  templateUrl: "./professional-list.component.html",
  styleUrls: ["./professional-list.component.scss"],
})
export class ProfessionalListComponent implements OnInit {
  professionals: ParamedicalProfessionalDTO[] = []
  specialtyFilter = ""
  specialties: string[] = []
  loading = false
  error: string | null = null

  constructor(
    private paramedicalService: ParamedicalService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadProfessionals()
  }

  loadProfessionals(): void {
    this.loading = true
    this.error = null

    if (this.specialtyFilter) {
      this.paramedicalService.getProfessionalsBySpecialty(this.specialtyFilter).subscribe({
        next: (data) => {
          this.professionals = data
          this.loading = false
        },
        error: (err) => {
          this.error = "Failed to load professionals. Please try again."
          this.loading = false
          console.error(err)
        },
      })
    } else {
      this.paramedicalService.getAllProfessionals().subscribe({
        next: (data) => {
          this.professionals = data
          this.extractSpecialties()
          this.loading = false
        },
        error: (err) => {
          this.error = "Failed to load professionals. Please try again."
          this.loading = false
          console.error(err)
        },
      })
    }
  }

  extractSpecialties(): void {
    const specialtySet = new Set<string>()
    this.professionals.forEach((prof) => {
      if (prof.specialty) {
        specialtySet.add(prof.specialty)
      }
    })
    this.specialties = Array.from(specialtySet)
  }

  filterBySpecialty(specialty: string): void {
    this.specialtyFilter = specialty
    this.loadProfessionals()
  }

  clearFilter(): void {
    this.specialtyFilter = ""
    this.loadProfessionals()
  }

  viewDetails(id: string): void {
    this.router.navigate(["/paramedical/detail", id])
  }

  editProfessional(id: string): void {
    this.router.navigate(["/paramedical/edit", id])
  }

  deleteProfessional(id: string): void {
    if (confirm("Are you sure you want to delete this professional?")) {
      this.paramedicalService.deleteProfessional(id).subscribe({
        next: () => {
          this.loadProfessionals()
        },
        error: (err) => {
          this.error = "Failed to delete professional. Please try again."
          console.error(err)
        },
      })
    }
  }

  createProfessional(): void {
    this.router.navigate(["/paramedical/create"])
  }

  findNearby(): void {
    this.router.navigate(["/paramedical/nearby"])
  }
}
