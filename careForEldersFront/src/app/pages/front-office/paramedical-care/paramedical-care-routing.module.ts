import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { DoctorServicesComponent } from "./doctor-services/doctor-services.component"
import { SoignantRequestsComponent } from "./soignant-requests/soignant-requests.component"
import { UserServicesComponent } from "./user-services/user-services.component"
import { UserRequestsComponent } from "./user-requests/user-requests.component"
import { ParamedicalMapComponent } from "./paramedical-map/paramedical-map.component";
import { AppointmentFormComponent } from './appointment-form/appointment-form.component';
import { AppointmentsComponent } from './appointments/appointments.component';
import { NearbyProfessionalsComponent } from './nearby-professionals/nearby-professionals.component';
import { ProfessionalDetailComponent } from './professional-detail/professional-detail.component';
import { ProfessionalFormComponent } from './professional-form/professional-form.component';
import { ProfessionalListComponent } from './professional-list/professional-list.component';
import { ParamedicalCareComponent } from './paramedicalcare.component';

const routes: Routes = [
  {
    path: '',
    component: ParamedicalCareComponent,
    children: [
      { path: '', redirectTo: 'professional-list', pathMatch: 'full' },
      { path: 'professional-list', component: ProfessionalListComponent },
      { path: 'appointment', component: AppointmentsComponent },
      { path: 'nearby-professionals', component: NearbyProfessionalsComponent },
      { path: 'paramedical-map', component: ParamedicalMapComponent },
      { path: 'professional-detail', component: ProfessionalDetailComponent },
      { path: 'professional-form', component: ProfessionalFormComponent },
      { path: 'appointment-form', component: AppointmentFormComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ParamedicalCareRoutingModule {}
