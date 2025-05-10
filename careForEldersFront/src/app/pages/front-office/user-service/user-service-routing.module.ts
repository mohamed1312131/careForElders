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

import { UserServicesComponent } from '../paramedical-care/user-services/user-services.component';
import { UserRequestsComponent } from '../paramedical-care/user-requests/user-requests.component';
import { SoignantRequestsComponent } from '../paramedical-care/soignant-requests/soignant-requests.component';
import { PatientBillFormComponent } from '../patient-bill/patient-bill-form/patient-bill-form.component';
import { PatientBillPaymentComponent } from '../patient-bill/patient-bill-payment/patient-bill-payment.component';
import { PatientBillHistoryComponent } from '../patient-bill/patient-bill-history/patient-bill-history.component';
import { PostDetailComponent } from '../blog-forum/post-detail/post-detail.component';
import { PostFormComponent } from '../blog-forum/post-form/post-form.component';
import { PostListComponent } from '../blog-forum/post-list/post-list.component';

import {AdminDashboardComponent} from "../nutrition/admin-dashboard/admin-dashboard.component";





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

{
    path: "edit/:id",
    component: PatientBillFormComponent,
  },
  {
    path: "view/:id",
    component: PatientBillFormComponent, // You can create a dedicated view component later
  },
  // Payment related routes
  {
    path: "payment/:id",
    component: PatientBillPaymentComponent,
    //data: { title: "Process Payment" },
  },
  {
    path: "history/:id",
    component: PatientBillHistoryComponent,
    //data: { title: "Payment History" },
  },
      {
        path: 'create',
        component: PatientBillFormComponent
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
      {
        path:'Abonnement',
        component: AbonnementTypeComponent
      },
      {

        path:'userServices',
        component: UserServicesComponent
      },
      {
        path:'SoignantRequests',
        component: SoignantRequestsComponent
      },
      {
        path:'UserRequests',
        component: UserRequestsComponent
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

      { path: "blog", component: PostListComponent },
            { path: "post/create", component: PostFormComponent },
            { path: "post/edit/:id", component: PostFormComponent },
            { path: "post/:id", component: PostDetailComponent },

      {path:'nutrition',
      children: [
        {
          path:'nutritionplanlist',
          component:PlanListComponent,
        },
        {
          path:'nutritionplandetails/:id',
          component:PlanDetailsComponent ,
        },
        {

          path:'nutritionplainadmin',
          component:AdminDashboardComponent,
        }
      ]}

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
