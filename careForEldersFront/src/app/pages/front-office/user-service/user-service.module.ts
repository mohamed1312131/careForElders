import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersComponent } from './user/user.component';
import { UserServiceRoutingModule } from './user-service-routing.module';
import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { AppointmentAvailabilityModule } from '../appointment-availability/appointment-availability.module';
import { SubscriptionModule } from '../subscription/subscription.module';
import { ParamedicalCareModule } from '../paramedical-care/paramedical-care.module';
import { RouterModule } from '@angular/router';


@NgModule({

  declarations: [UsersComponent,UserLayoutComponent],
  imports: [
    CommonModule,
    UserServiceRoutingModule,
    AppointmentAvailabilityModule,
    SubscriptionModule,
    ParamedicalCareModule,
    RouterModule

  ]
})
export class UserModule { }

