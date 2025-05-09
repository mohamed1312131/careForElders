import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

//import { UsersComponent } from './user/user.component';

import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { UserinfoComponent } from './userinfo/userinfo.component';
import { ChatAIComponent } from '../chat/chat-ai/chat-ai.component';
import { TestingComponent } from '../appointment-availability/testing/testing.component';
import { SearchDoctorComponent } from '../appointment-availability/search-doctor/search-doctor.component';
import { DoctorDetailsComponent } from '../appointment-availability/doctor-details/doctor-details.component';
import { AddAvailabilityComponent } from '../appointment-availability/add-availability/add-availability.component';
import { AbonnementTypeComponent } from '../subscription/abonnement-type/abonnement-type.component';
import { MyReservationsComponent } from '../appointment-availability/my-reservations/my-reservations.component';



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
      {path:'search',
        component: SearchDoctorComponent
      },
      {path:'doctor/:id',
        component: DoctorDetailsComponent
      },
      {path:'doctor/:id/AddAvailability',
        component: AddAvailabilityComponent
      },
      {path:'Reservation',
        component: MyReservationsComponent
      },
      {path:'Abonnement',
        component: AbonnementTypeComponent
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
