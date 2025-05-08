import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersComponent } from './user/user.component';
import { UserServiceRoutingModule } from './user-service-routing.module';


@NgModule({
  declarations: [UsersComponent],
  imports: [
    CommonModule,
    UserServiceRoutingModule
  ]
})
export class UserModule { }
