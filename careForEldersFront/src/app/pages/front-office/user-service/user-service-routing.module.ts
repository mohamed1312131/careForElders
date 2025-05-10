import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsersComponent} from './user/user.component';

//import { UsersComponent } from './user/user.component';

import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { UserinfoComponent } from './userinfo/userinfo.component';
import { ChatAIComponent } from '../chat/chat-ai/chat-ai.component';
import { PatientBillListComponent } from '../patient-bill/patient-bill-list/patient-bill-list.component';
import { TestingComponent } from '../appointment-availability/testing/testing.component';
import { SearchDoctorComponent } from '../appointment-availability/search-doctor/search-doctor.component';
import { DoctorDetailsComponent } from '../appointment-availability/doctor-details/doctor-details.component';
import { AddAvailabilityComponent } from '../appointment-availability/add-availability/add-availability.component';
import { AbonnementTypeComponent } from '../subscription/abonnement-type/abonnement-type.component';
import { MyReservationsComponent } from '../appointment-availability/my-reservations/my-reservations.component';
import { DoctorAddProgramComponent } from '../plan-and-exercise/doctor/doctor-add-program/doctor-add-program.component';
import { DoctorPlanListComponent } from '../plan-and-exercise/doctor/doctor-plan-list/doctor-plan-list.component';
import { PlanListComponent } from '../plan-and-exercise/plan-list/plan-list.component';
import { PlanDetailsComponent } from '../plan-and-exercise/plan-details/plan-details.component';
import { ProgramComponent } from '../plan-and-exercise/program/program.component';
import { AddExerciseComponent } from '../plan-and-exercise/doctor/add-exercise/add-exercise.component';




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
      },{
        path: 'bill',
        component: PatientBillListComponent
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
      },
      {
        path: 'plan',
        children: [
          {
            path:'add-program',
            component: DoctorAddProgramComponent
          },
          {
            path:'list',
            component: DoctorPlanListComponent
          },
          {
            path:'userprogram',
            component: PlanListComponent
          },
          {
            path: 'plandetails/:programId',
            component: PlanDetailsComponent,
          },
          {
            path:'program/:assignmentId/day/:dayNumber',
            component: ProgramComponent,
          },
          {
            path:'addExercise',
            component: AddExerciseComponent,
          },
        ]
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
