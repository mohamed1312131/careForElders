import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { PatientBillListComponent } from "./patient-bill-list/patient-bill-list.component"
import { PatientBillFormComponent } from "./patient-bill-form/patient-bill-form.component"
import { PatientBillPaymentComponent } from "./patient-bill-payment/patient-bill-payment.component"
import { PatientBillHistoryComponent } from "./patient-bill-history/patient-bill-history.component"
//import { ReceiptTestComponent } from "./receipt-test/receipt-test.component"

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
  // Payment related routes
  {
    path: "payment/:id",
    component: PatientBillPaymentComponent,
    //data: { title: "Process Payment" },
  },
  {
    path: "history/:id",
    component: PatientBillHistoryComponent,
    //data: { title: "Payment History" },
  },
  {
   path: "history/:id", component: PatientBillHistoryComponent },
  
  
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PatientBillRoutingModule {}
