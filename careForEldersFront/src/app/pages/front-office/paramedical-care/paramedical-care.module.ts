import { NgModule } from "@angular/core"
import { CommonModule } from "@angular/common"
import { ReactiveFormsModule, FormsModule } from "@angular/forms"
import { HttpClientModule } from "@angular/common/http"
import { RouterModule } from "@angular/router"

import { ParamedicalCareRoutingModule } from "./paramedical-care-routing.module"
import { DoctorServicesComponent } from "./doctor-services/doctor-services.component"
import { SoignantRequestsComponent } from "./soignant-requests/soignant-requests.component"
import { UserServicesComponent } from "./user-services/user-services.component"
import { RequestServiceDialog } from "./request-service-dialog/request-service-dialog.component"
import { UserRequestsComponent } from "./user-requests/user-requests.component"
import { ServiceDetailsDialog } from "./service-details-dialog/service-details-dialog.component"
import { ParamedicalMapComponent } from "./paramedical-map/paramedical-map.component"

import { MatChipsModule } from "@angular/material/chips"
import { MatDialogModule } from "@angular/material/dialog"
import { MaterialModule } from "src/app/material.module"

// Add component imports
import { ProfessionalFormComponent } from './professional-form/professional-form.component';
import { ProfessionalListComponent } from './professional-list/professional-list.component';
import { NearbyProfessionalsComponent } from './nearby-professionals/nearby-professionals.component';
import { ProfessionalDetailComponent } from './professional-detail/professional-detail.component';
import { AppointmentFormComponent } from './appointment-form/appointment-form.component';
import { AppointmentsComponent } from './appointments/appointments.component';
import { ParamedicalCareComponent } from './paramedicalcare.component';

@NgModule({
  declarations: [
    UserServicesComponent,
    RequestServiceDialog,
    UserRequestsComponent,
    SoignantRequestsComponent,
    DoctorServicesComponent,
    ServiceDetailsDialog,
    ParamedicalMapComponent,
    ProfessionalFormComponent,
    ProfessionalListComponent,
    NearbyProfessionalsComponent,
    ProfessionalDetailComponent,
    AppointmentFormComponent,
    ParamedicalCareComponent,
    AppointmentsComponent
  ],
  imports: [
    CommonModule,
    ParamedicalCareRoutingModule,
    MaterialModule,
    HttpClientModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MatChipsModule,
    MatDialogModule,
  ],
  exports: [
    ParamedicalMapComponent,
    ParamedicalCareComponent,
    AppointmentFormComponent,
  ],
})

export class ParamedicalCareModule {}
