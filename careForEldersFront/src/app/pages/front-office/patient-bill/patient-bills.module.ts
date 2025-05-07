// src/app/pages/patient-bills/patient-bills.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { SharedModule } from '../../../shared/shared.module';
import { PatientBillListComponent } from './patient-bill-list/patient-bill-list.component';
import { PatientBillDetailComponent } from './patient-bill-detail/patient-bill-detail.component';
import { PatientBillFormComponent } from './patient-bill-form/patient-bill-form.component';

const routes: Routes = [
  { path: '', component: PatientBillListComponent },
  { path: 'new', component: PatientBillFormComponent },
  { path: 'edit/:id', component: PatientBillFormComponent },
  { path: ':id', component: PatientBillDetailComponent }
];

@NgModule({
  declarations: [
    PatientBillListComponent,
    PatientBillDetailComponent,
    PatientBillFormComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule
  ]
})
export class PatientBillsModule { }