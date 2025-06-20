export interface ParamedicalProfessionalDTO {
  id?: string
  name: string
  specialty: string
  contactInfo: string
  createdAt?: Date
  updatedAt?: Date
  distance?: number
  latitude?: number
  longitude?: number
}

export interface CreateProfessionalRequest {
  name: string
  specialty: string
  contactInfo: string
  address?: string
}

export interface UpdateProfessionalRequest {
  name?: string
  specialty?: string
  contactInfo?: string
}

export interface AppointmentDTO {
  id?: string
  elderId: string
  professionalId: string
  professionalName?: string
  specialty?: string
  appointmentTime: Date
  location: string
  notes?: string
  status: string
}

export interface AppointmentRequest {
  elderId: string
  professionalId: string
  appointmentTime: Date
  location?: string
  notes?: string
}
