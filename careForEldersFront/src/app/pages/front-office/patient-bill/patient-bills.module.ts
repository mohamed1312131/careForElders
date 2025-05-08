import { NgModule } from "@angular/core"
import { CommonModule } from "@angular/common"
import { ReactiveFormsModule } from "@angular/forms"
import { RouterModule } from "@angular/router"
import { PatientBillService } from "./patient-bill.service";
// Angular Material Imports
import { MatCardModule } from "@angular/material/card"
import { MatInputModule } from "@angular/material/input"
import { MatFormFieldModule } from "@angular/material/form-field"
import { MatSelectModule } from "@angular/material/select"
import { MatDatepickerModule } from "@angular/material/datepicker"
import { MatNativeDateModule } from "@angular/material/core"
import { MatButtonModule } from "@angular/material/button"
import { MatIconModule } from "@angular/material/icon"
import { MatTableModule } from "@angular/material/table"
import { MatPaginatorModule } from "@angular/material/paginator"
import { MatSortModule } from "@angular/material/sort"
import { MatProgressBarModule } from "@angular/material/progress-bar"
import { MatSnackBarModule } from "@angular/material/snack-bar"
import { MatTooltipModule } from "@angular/material/tooltip"
import { HttpClientModule } from "@angular/common/http";
// Components
import { PatientBillFormComponent } from "./patient-bill-form/patient-bill-form.component"
import { PatientBillListComponent } from "./patient-bill-list/patient-bill-list.component"
import { PatientBillRoutingModule } from "./patient-bill-routing.module"



@NgModule({
  declarations: [PatientBillFormComponent, PatientBillListComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    PatientBillRoutingModule,
    HttpClientModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressBarModule,
    MatSnackBarModule,
    MatTooltipModule,
  ],
  exports: [PatientBillFormComponent, PatientBillListComponent],
  providers: [PatientBillService],
})
export class PatientBillModule {}
