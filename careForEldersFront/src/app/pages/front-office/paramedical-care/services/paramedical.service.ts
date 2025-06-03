import { Injectable } from "@angular/core"
import {  HttpClient, HttpParams } from "@angular/common/http"
import  { Observable } from "rxjs"
import  {
  ParamedicalProfessionalDTO,
  CreateProfessionalRequest,
  UpdateProfessionalRequest,
  AppointmentDTO,
  AppointmentRequest,
} from "../models/paramedical-professional.model"

@Injectable({
  providedIn: "root",
})
export class ParamedicalService {
  private apiUrl = "http://localhost:8086/api/paramedical"

  constructor(private http: HttpClient) {}

  // Professional CRUD operations
  getAllProfessionals(): Observable<ParamedicalProfessionalDTO[]> {
    return this.http.get<ParamedicalProfessionalDTO[]>(this.apiUrl)
  }

  getProfessionalById(id: string): Observable<ParamedicalProfessionalDTO> {
    return this.http.get<ParamedicalProfessionalDTO>(`${this.apiUrl}/${id}`)
  }

  getProfessionalsBySpecialty(specialty: string): Observable<ParamedicalProfessionalDTO[]> {
    return this.http.get<ParamedicalProfessionalDTO[]>(`${this.apiUrl}/specialty/${specialty}`)
  }

  createProfessional(request: CreateProfessionalRequest): Observable<ParamedicalProfessionalDTO> {
    return this.http.post<ParamedicalProfessionalDTO>(this.apiUrl, request)
  }

  updateProfessional(id: string, request: UpdateProfessionalRequest): Observable<ParamedicalProfessionalDTO> {
    return this.http.put<ParamedicalProfessionalDTO>(`${this.apiUrl}/${id}`, request)
  }

  partialUpdateProfessional(id: string, request: UpdateProfessionalRequest): Observable<ParamedicalProfessionalDTO> {
    return this.http.patch<ParamedicalProfessionalDTO>(`${this.apiUrl}/${id}`, request)
  }

  deleteProfessional(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }

  // Location-based operations
  findNearbyProfessionals(
    location: string,
    specialty?: string,
    distanceKm = 10,
  ): Observable<ParamedicalProfessionalDTO[]> {
    let params = new HttpParams().set("location", location).set("distanceKm", distanceKm.toString())

    if (specialty) {
      params = params.set("specialty", specialty)
    }

    return this.http.get<ParamedicalProfessionalDTO[]>(`${this.apiUrl}/nearby`, { params })
  }

  // Appointment operations
  scheduleAppointment(request: AppointmentRequest): Observable<AppointmentDTO> {
    return this.http.post<AppointmentDTO>(`${this.apiUrl}/appointments`, request)
  }

  getElderAppointments(elderId: string): Observable<AppointmentDTO[]> {
    return this.http.get<AppointmentDTO[]>(`${this.apiUrl}/elders/${elderId}/appointments`)
  }
}
