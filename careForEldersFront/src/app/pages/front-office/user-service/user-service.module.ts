import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserServiceRoutingModule } from './user-service-routing.module';
import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { ChatModule } from '../chat/chat.module';
import { AppointmentAvailabilityModule } from '../appointment-availability/appointment-availability.module';
import { SearchDoctorComponent } from '../appointment-availability/search-doctor/search-doctor.component';
import { PlanAndExerciseModule } from '../plan-and-exercise/plan-and-exercise.module';


@NgModule({
  declarations: [UserLayoutComponent],
  imports: [
    CommonModule,
    UserServiceRoutingModule,
    ChatModule,
    AppointmentAvailabilityModule,
    PlanAndExerciseModule,
  ]
})
export class UserModule { }
