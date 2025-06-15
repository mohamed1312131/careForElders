import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ParamedicalCareRoutingModule } from './paramedical-care-routing.module';
import { DoctorServicesComponent } from './doctor-services/doctor-services.component';
import { SoignantRequestsComponent } from './soignant-requests/soignant-requests.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { MaterialModule } from 'src/app/material.module';
import { UserServicesComponent } from './user-services/user-services.component';
import { RequestServiceDialog } from './request-service-dialog/request-service-dialog.component';
import { UserRequestsComponent } from './user-requests/user-requests.component';
import { ServiceDetailsDialog } from './service-details-dialog/service-details-dialog.component';


@NgModule({
  declarations: [

    UserServicesComponent,
    RequestServiceDialog,
    UserRequestsComponent,
    SoignantRequestsComponent,
    DoctorServicesComponent,
    ServiceDetailsDialog

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
  exports:[    UserServicesComponent,
    RequestServiceDialog,
    UserRequestsComponent,
    SoignantRequestsComponent,
    DoctorServicesComponent,
    ServiceDetailsDialog]
})
export class ParamedicalCareModule { }
