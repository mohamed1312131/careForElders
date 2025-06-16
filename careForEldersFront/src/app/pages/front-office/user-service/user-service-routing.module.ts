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
import {PatientBillFormComponent} from "../patient-bill/patient-bill-form/patient-bill-form.component";
import {PatientBillPaymentComponent} from "../patient-bill/patient-bill-payment/patient-bill-payment.component";
import {PatientBillHistoryComponent} from "../patient-bill/patient-bill-history/patient-bill-history.component";
import {MedicalRecordListComponent} from "../medical-record/medical-records-list/medical-records-list.component";
import {MedicalRecordComponent} from "../medical-record/medical-record/medical-record.component";
import { UnauthorizedComponent } from '../subscription/unauthorized/unauthorized.component';
import { MyScheduleComponent } from '../appointment-availability/my-schedule/my-schedule.component';
import { SubscriptionGuard } from '../subscription/subscriptionguard';




const routes: Routes = [
  {

    path: '',
    component: UserLayoutComponent,
  },
  {
    path: 'userProfile',
    component: UserLayoutComponent,
    children: [

      // Basic Plan Features (available to all)
      { path: 'search', component: SearchDoctorComponent },
      { path: 'doctor/:id', component: DoctorDetailsComponent },
      { path: 'Reservation', component: MyReservationsComponent },
      { path: 'Abonnement', component: AbonnementTypeComponent },
      { path: 'unauthorized', component: UnauthorizedComponent },

      // Doctor-specific features
      { path: 'doctor/:id/AddAvailability', component: AddAvailabilityComponent },
      { path: 'mySchedule', component: MyScheduleComponent },

      // Silver Plan Features
      { 
        path: 'AI', 
        component: ChatAIComponent, 
        /*canActivate: [SubscriptionGuard], 
        data: { modules: 'Chat with doctors' } */
      },
      { 
        path: 'bill', 
        component: PatientBillListComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'patientBill' } 
      },
      { 
        path: 'create', 
        component: PatientBillFormComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'patientBill' } 
      },
      { 
        path: 'edit/:id', 
        component: PatientBillFormComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'patientBill' } 
      },
      { 
        path: 'view/:id', 
        component: PatientBillFormComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'patientBill' } 
      },
      { 
        path: 'payment/:id', 
        component: PatientBillPaymentComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'patientBill' } 
      },
      { 
        path: 'history/:id', 
        component: PatientBillHistoryComponent, 
        canActivate: [SubscriptionGuard], 
        data: { modules: 'patientBill' } 

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
      {
        path: 'userinfo/:id',
        component: UserinfoComponent,

      },
      {
        path: 'users',
        component: UsersComponent,

      },


      {path:'medicalRecord',
        children: [
          {
            path:'medicalrecordlist',
            component:MedicalRecordListComponent,

          },
          {
            path:'medicalrecord/:id',
            component:MedicalRecordComponent ,
          },
        ]},

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
