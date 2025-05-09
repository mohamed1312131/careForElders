import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserServiceRoutingModule } from './user-service-routing.module';
import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { ChatModule } from '../chat/chat.module';
//import { PatientBillsModule } from '../patient-bill/patient-bills.module';


@NgModule({
  declarations: [UserLayoutComponent],
  imports: [
    CommonModule,
    UserServiceRoutingModule,
    ChatModule,
   // PatientBillsModule
  ]
})
export class UserModule { }
