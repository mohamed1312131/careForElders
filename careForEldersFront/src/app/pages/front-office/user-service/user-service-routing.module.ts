import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UsersComponent } from './user/user.component';

import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { UserinfoComponent } from './userinfo/userinfo.component';
import { ChatAIComponent } from '../chat/chat-ai/chat-ai.component';



const routes: Routes = [
  {

    path: '',
    component: UsersComponent,
  },
{
    path: 'userProfile',
    component: UserLayoutComponent,
    children: [ 
      {
        path: 'AI', 
        component: ChatAIComponent
      },
    ]
  },
  {
    path: 'userinfo/:id',
    component: UserinfoComponent,
  },


];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserServiceRoutingModule { }
