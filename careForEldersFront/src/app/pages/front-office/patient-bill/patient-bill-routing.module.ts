import { NgModule } from "@angular/core"
import { RouterModule,  Routes } from "@angular/router"
import { PatientBillListComponent } from "./patient-bill-list/patient-bill-list.component"
import { PatientBillFormComponent } from "./patient-bill-form/patient-bill-form.component"

const routes: Routes = [
  {
    path: "",
    component: PatientBillListComponent,
  },
  {
    path: "create",
    component: PatientBillFormComponent,
  },
  {
    path: "edit/:id",
    component: PatientBillFormComponent,
  },
  {
    path: "view/:id",
    component: PatientBillFormComponent, // You can create a dedicated view component later
  },
  
 
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PatientBillRoutingModule {}
