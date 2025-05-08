import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

//import { UsersComponent } from './user/user.component';

import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { UserinfoComponent } from './userinfo/userinfo.component';
import { ChatAIComponent } from '../chat/chat-ai/chat-ai.component';
import { TestingComponent } from '../appointment-availability/testing/testing.component';



const routes: Routes = [
  {

    path: '',
    component: UserLayoutComponent,
  },
{
    path: 'userProfile',
    component: UserLayoutComponent,
    children: [ 
      {
        path: 'AI', 
        component: ChatAIComponent
      },
      {path:'testing',
        component: TestingComponent
      }
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
