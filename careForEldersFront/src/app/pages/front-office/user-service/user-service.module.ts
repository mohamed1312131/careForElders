import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user/user.component';
import { UserServiceRoutingModule } from './user-service-routing.module';


@NgModule({
  declarations: [UserComponent],
  imports: [
    CommonModule,
    UserServiceRoutingModule
  ]
})
export class UserModule { }
