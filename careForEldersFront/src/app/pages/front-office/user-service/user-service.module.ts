import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {  UsersComponent } from './user/user.component';
import { UserServiceRoutingModule } from './user-service-routing.module';





//import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { AppointmentAvailabilityModule } from '../appointment-availability/appointment-availability.module';
import { SubscriptionModule } from '../subscription/subscription.module';
//import { ParamedicalCareModule } from '../paramedical-care/paramedical-care.module';
import { RouterModule } from '@angular/router';
import { BlogForumModule } from '../blog-forum/blog-forum.module';
import { FormsModule } from '@angular/forms';
import { ChatModule } from '../chat/chat.module';
import { ParamedicalCareModule } from '../paramedical-care/paramedical-care.module';
//import { ParamedicalModule } from '../paramedical-care/paramedical-care.module';


@NgModule({

 declarations: [UsersComponent,],
    imports: [
        CommonModule,
        UserServiceRoutingModule,
        ChatModule,
        AppointmentAvailabilityModule,
        SubscriptionModule,
        ParamedicalCareModule,
        RouterModule,
        FormsModule

    ]
})
export class UserModule { }
