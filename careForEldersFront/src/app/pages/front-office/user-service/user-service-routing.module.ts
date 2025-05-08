import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
<<<<<<< HEAD
import { UsersComponent } from './user/user.component';
=======
import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { UserinfoComponent } from './userinfo/userinfo.component';
import { ChatAIComponent } from '../chat/chat-ai/chat-ai.component';
>>>>>>> origin/master


const routes: Routes = [
  {
<<<<<<< HEAD
    path: '',
    component: UsersComponent,
  }
=======
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

>>>>>>> origin/master
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserServiceRoutingModule { }
