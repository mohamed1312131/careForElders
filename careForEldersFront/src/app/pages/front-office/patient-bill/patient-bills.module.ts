import { NgModule } from "@angular/core"
import { CommonModule } from "@angular/common"
import { RouterModule } from "@angular/router"
import { ReactiveFormsModule, FormsModule } from "@angular/forms"
import { HttpClientModule } from '@angular/common/http';
// Material Imports
import { MatTableModule } from "@angular/material/table"
import { MatPaginatorModule } from "@angular/material/paginator"
import { MatSortModule } from "@angular/material/sort"
import { MatInputModule } from "@angular/material/input"
import { MatButtonModule } from "@angular/material/button"
import { MatIconModule } from "@angular/material/icon"
import { MatSnackBarModule } from "@angular/material/snack-bar"
import { MatProgressBarModule } from "@angular/material/progress-bar"
import { MatFormFieldModule } from "@angular/material/form-field"
import { MatSelectModule } from "@angular/material/select"
import { MatTooltipModule } from "@angular/material/tooltip"
import { MatCardModule } from "@angular/material/card"
import { MatDividerModule } from "@angular/material/divider"
import { MatDatepickerModule } from "@angular/material/datepicker"
import { MatNativeDateModule } from "@angular/material/core"

// Components
import { PatientBillListComponent } from "./patient-bill-list/patient-bill-list.component"
import { PatientBillFormComponent } from "./patient-bill-form/patient-bill-form.component"
import { PatientBillPaymentComponent } from "./patient-bill-payment/patient-bill-payment.component"
import { PatientBillRoutingModule } from "./patient-bill-routing.module"
import { PatientBillHistoryComponent } from "./patient-bill-history/patient-bill-history.component";

// Routes


@NgModule({
  declarations: [PatientBillListComponent, PatientBillFormComponent, PatientBillPaymentComponent,PatientBillHistoryComponent],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    PatientBillRoutingModule,
    // Material modules
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatSelectModule,
    MatTooltipModule,
    MatCardModule,
    MatDividerModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  exports: [PatientBillListComponent, PatientBillFormComponent, PatientBillPaymentComponent,PatientBillHistoryComponent],
})
export class PatientBillsModule {}
