import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
<<<<<<< HEAD
import { UsersComponent } from './user/user.component';
=======
>>>>>>> origin/master
import { UserServiceRoutingModule } from './user-service-routing.module';
import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { ChatModule } from '../chat/chat.module';


@NgModule({
<<<<<<< HEAD
  declarations: [UsersComponent],
=======
  declarations: [UserLayoutComponent],
>>>>>>> origin/master
  imports: [
    CommonModule,
    UserServiceRoutingModule,
    ChatModule
  ]
})
export class UserModule { }
