import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AppointmentAvailabilityRoutingModule } from './appointment-availability-routing.module';
import { TestingComponent } from './testing/testing.component';
<<<<<<< Updated upstream


@NgModule({
  declarations: [TestingComponent],
=======
import { MaterialModule } from 'src/app/material.module';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SearchDoctorComponent } from './search-doctor/search-doctor.component';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { DoctorDetailsComponent } from './doctor-details/doctor-details.component';
import { MyReservationsComponent } from './my-reservations/my-reservations.component';
import { MyScheduleComponent } from './my-schedule/my-schedule.component';
import { AppointmentDialogComponent } from './appointment-dialog/appointment-dialog.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDividerModule } from '@angular/material/divider';
import { MatCardModule } from '@angular/material/card';
import { EventDetailsDialogComponent } from './event-details-dialog/event-details-dialog.component';
import { JitsiDialogComponent } from './jitsi-dialog/jitsi-dialog.component';
import { LoadingComponent } from './loading/loading.component';
import { LoadingStepBlockComponent } from './loading-step-block/loading-step-block.component';


@NgModule({
  declarations: [TestingComponent,
    SearchDoctorComponent,
    DoctorDetailsComponent,
    DoctorDetailsComponent,
    MyReservationsComponent,
  MyScheduleComponent,
  AppointmentDialogComponent,
EventDetailsDialogComponent,
JitsiDialogComponent,
LoadingComponent,
LoadingStepBlockComponent],
  
>>>>>>> Stashed changes
  imports: [
    CommonModule,
    AppointmentAvailabilityRoutingModule
  ],
  exports:[TestingComponent]
})
export class AppointmentAvailabilityModule { }
