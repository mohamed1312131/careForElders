import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AppointmentAvailabilityRoutingModule } from './appointment-availability-routing.module';
import { TestingComponent } from './testing/testing.component';
import { MaterialModule } from 'src/app/material.module';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SearchDoctorComponent } from './search-doctor/search-doctor.component';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { DoctorDetailsComponent } from './doctor-details/doctor-details.component';


@NgModule({
  declarations: [TestingComponent,SearchDoctorComponent,DoctorDetailsComponent,DoctorDetailsComponent],
  imports: [
    CommonModule,
    AppointmentAvailabilityRoutingModule,
       
        MaterialModule,
        HttpClientModule,
        RouterModule,
        FormsModule,
        ReactiveFormsModule,
        CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }),
  ],
  exports:[TestingComponent]
})
export class AppointmentAvailabilityModule { }
