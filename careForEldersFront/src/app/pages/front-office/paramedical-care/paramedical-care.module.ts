<<<<<<< Updated upstream
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ParamedicalCareRoutingModule } from './paramedical-care-routing.module';
=======
import { NgModule } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule, ReactiveFormsModule } from "@angular/forms"
import { HttpClientModule } from "@angular/common/http"
>>>>>>> Stashed changes

import { ParamedicalRoutingModule } from "./paramedical-care-routing.module"
import { ProfessionalListComponent } from "./professional-list/professional-list.component"
import { ProfessionalDetailComponent } from "./professional-detail/professional-detail.component"
import { ProfessionalFormComponent } from "./professional-form/professional-form.component"
import { NearbyProfessionalsComponent } from "./nearby-professionals/nearby-professionals.component"
import { AppointmentsComponent } from "./appointments/appointments.component"
import { AppointmentFormComponent } from "./appointment-form/appointment-form.component"

@NgModule({
<<<<<<< Updated upstream
  declarations: [],
  imports: [
    CommonModule,
    ParamedicalCareRoutingModule
  ]
=======
  declarations: [
    ProfessionalListComponent,
    ProfessionalDetailComponent,
    ProfessionalFormComponent,
    NearbyProfessionalsComponent,
    AppointmentsComponent,
    AppointmentFormComponent,
  ],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule, ParamedicalRoutingModule],
>>>>>>> Stashed changes
})
export class ParamedicalModule {}
