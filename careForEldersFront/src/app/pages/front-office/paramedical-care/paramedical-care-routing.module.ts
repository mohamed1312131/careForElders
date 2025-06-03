import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { ProfessionalListComponent } from "./professional-list/professional-list.component"
import { ProfessionalDetailComponent } from "./professional-detail/professional-detail.component"
import { ProfessionalFormComponent } from "./professional-form/professional-form.component"
import { NearbyProfessionalsComponent } from "./nearby-professionals/nearby-professionals.component"
import { AppointmentsComponent } from "./appointments/appointments.component"

const routes: Routes = [
  {
    path: "paramedical",
    children: [
      { path: "", redirectTo: "list", pathMatch: "full" },
      { path: "list", component: ProfessionalListComponent },
      { path: "detail/:id", component: ProfessionalDetailComponent },
      { path: "create", component: ProfessionalFormComponent },
      { path: "edit/:id", component: ProfessionalFormComponent },
      { path: "nearby", component: NearbyProfessionalsComponent },
      { path: "appointments", component: AppointmentsComponent },
      { path: "appointments/:elderId", component: AppointmentsComponent },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ParamedicalRoutingModule {}
