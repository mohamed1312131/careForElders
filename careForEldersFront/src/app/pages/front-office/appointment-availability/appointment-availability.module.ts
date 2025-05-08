import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AppointmentAvailabilityRoutingModule } from './appointment-availability-routing.module';
import { TestingComponent } from './testing/testing.component';


@NgModule({
  declarations: [TestingComponent],
  imports: [
    CommonModule,
    AppointmentAvailabilityRoutingModule
  ],
  exports:[TestingComponent]
})
export class AppointmentAvailabilityModule { }
