import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsersComponent } from './user/user.component';
import { UserLayoutComponent } from './userProfile/user-layout/user-layout.component';
import { UserinfoComponent } from './userinfo/userinfo.component';
import { ChatAIComponent } from '../chat/chat-ai/chat-ai.component';
import { PatientBillListComponent } from '../patient-bill/patient-bill-list/patient-bill-list.component';
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




 ebe25a6 (change)
import { MyScheduleComponent } from '../appointment-availability/my-schedule/my-schedule.component';
import { UserServicesComponent } from '../paramedical-care/user-services/user-services.component';
import { UserRequestsComponent } from '../paramedical-care/user-requests/user-requests.component';
import { SoignantRequestsComponent } from '../paramedical-care/soignant-requests/soignant-requests.component';
import { PatientBillFormComponent } from '../patient-bill/patient-bill-form/patient-bill-form.component';
import { PatientBillPaymentComponent } from '../patient-bill/patient-bill-payment/patient-bill-payment.component';
import { PatientBillHistoryComponent } from '../patient-bill/patient-bill-history/patient-bill-history.component';
import { PostDetailComponent } from '../blog-forum/post-detail/post-detail.component';
import { PostFormComponent } from '../blog-forum/post-form/post-form.component';
import { PostListComponent } from '../blog-forum/post-list/post-list.component';
import { AdminDashboardComponent } from "../nutrition/admin-dashboard/admin-dashboard.component";
import { UnauthorizedComponent } from '../subscription/unauthorized/unauthorized.component';
import { SubscriptionGuard } from '../subscription/subscriptionguard';
import { MedicalRecordComponent } from '../medical-record/medical-record/medical-record.component';
import { MedicalRecordListComponent } from '../medical-record/medical-records-list/medical-records-list.component';

import { AppointmentFormComponent } from '../paramedical-care/appointment-form/appointment-form.component';
import { AppointmentsComponent } from '../paramedical-care/appointments/appointments.component';
import { NearbyProfessionalsComponent } from '../paramedical-care/nearby-professionals/nearby-professionals.component';
import { ParamedicalMapComponent } from '../paramedical-care/paramedical-map/paramedical-map.component';
import { ProfessionalDetailComponent } from '../paramedical-care/professional-detail/professional-detail.component';
import { ProfessionalFormComponent } from '../paramedical-care/professional-form/professional-form.component';
import { ProfessionalListComponent } from '../paramedical-care/professional-list/professional-list.component';
import {RoleGuard} from "../../../guards/role.guard";

import {PatientBillFormComponent} from "../patient-bill/patient-bill-form/patient-bill-form.component";
import {PatientBillPaymentComponent} from "../patient-bill/patient-bill-payment/patient-bill-payment.component";
import {PatientBillHistoryComponent} from "../patient-bill/patient-bill-history/patient-bill-history.component";
import {MedicalRecordListComponent} from "../medical-record/medical-records-list/medical-records-list.component";
import {MedicalRecordComponent} from "../medical-record/medical-record/medical-record.component";
import { UnauthorizedComponent } from '../subscription/unauthorized/unauthorized.component';
import { MyScheduleComponent } from '../appointment-availability/my-schedule/my-schedule.component';
import { SubscriptionGuard } from '../subscription/subscriptionguard';
 5ef8240 ( event management and reminder for the events , notification part is done)

 ebe25a6 (change)

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
        path: 'nutrition',
        loadChildren: () => import('../nutrition/nutrition.module').then(m => m.NutritionModule),
      },

 ebe25a6 (change)
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
        canActivate: [SubscriptionGuard], 
        data: { modules: 'Chat with doctors' } 
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

      // Blog/Forum
      { 
        path: "post", 
        component: PostListComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'blogForum' } 
      },
      { 
        path: "post/create", 
        component: PostFormComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'blogForum' } 
      },
      { 
        path: "post/edit/:id", 
        component: PostFormComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'blogForum' } 
      },
      { 
        path: "post/:id", 
        component: PostDetailComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'blogForum' } 
      },

      // Nutrition & Plans

 da86154 (updates)
      {
        path: 'AI',
        component: ChatAIComponent
      },{
        path: 'bill',
        component: PatientBillListComponent

 ebe25a6 (change)
      },

      // Blog/Forum
      { 
        path: "post", 
        component: PostListComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'blogForum' } 
      },
      { 
        path: "post/create", 
        component: PostFormComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'blogForum' } 
      },
      { 
        path: "post/edit/:id", 
        component: PostFormComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'blogForum' } 
      },
      { 
        path: "post/:id", 
        component: PostDetailComponent, 
        //canActivate: [SubscriptionGuard], 
        data: { modules: 'blogForum' } 
      },

      // Nutrition & Plans
      {
        path: 'plan',
      
        children: [
          { path: 'add-program', component: DoctorAddProgramComponent },
          { path: 'list', component: DoctorPlanListComponent },
          { path: 'userprogram', component: PlanListComponent },
          { path: 'plandetails/:programId', component: PlanDetailsComponent },
          { path: 'program/:assignmentId/day/:dayNumber', component: ProgramComponent },
          { path: 'addExercise', component: AddExerciseComponent }
        ]
      },


      {
        path: 'nutrition',
        canActivate: [SubscriptionGuard],
        data: { modules: 'nutrition' },
        children: [
          { path: 'nutritionplanlist', component: PlanListComponent },
          { path: 'nutritionplandetails/:id', component: PlanDetailsComponent },
          { path: 'nutritionplainadmin', component: AdminDashboardComponent }
        ]
      },

      {
        path: 'users',
        component: UsersComponent,

      },


 da86154 (updates)

 ebe25a6 (change)
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


        // nutrition 
        {path:'nutrition',
          children: [
            {
              path:'admin-dashboard',
              component:AdminDashboardComponent,
            },
            {
              path:'plan-details',
              component:PlanDetailsComponent ,
            },
            {
              path:'plan-list',
              component:PlanListComponent ,
            }
          ]},
          
  
          // Paramedical
          {path:'paramedical-care',
          children: [
            {
              path:'appointment-form',
              component:AppointmentFormComponent,
            },
            {
              path:'appointment',
              component:AppointmentsComponent ,
            },
            {
              path:'nearby-professionals',
              component:NearbyProfessionalsComponent ,
            },
            {
              path:'paramedical-map',
              component:ParamedicalMapComponent ,
            },
            {
              path:'professional-detail',
              component:ProfessionalDetailComponent ,
            },
            {
              path:'professional-form',
              component:ProfessionalFormComponent ,
            },
            {
              path:'professional-list',
              component:ProfessionalListComponent ,
            }
          ]},
  
  
  

 ebe25a6 (change)
      // Gold Plan Features
      
      { 
        path: 'userServices', 
        component: UserServicesComponent, 
        canActivate: [SubscriptionGuard], 
        data: { modules: 'paramedicalCare' } 



      {path:'search',
        component: SearchDoctorComponent
 da86154 (updates)

 ebe25a6 (change)
      },
      { 
        path: 'SoignantRequests', 
        component: SoignantRequestsComponent, 
        canActivate: [SubscriptionGuard], 
        data: { modules: 'paramedicalCare' } 
      },
      { 
        path: 'UserRequests', 
        component: UserRequestsComponent, 
        canActivate: [SubscriptionGuard], 
        data: { modules: 'paramedicalCare' } 
      }
    ]
  },
  { path: 'userinfo/:id', component: UserinfoComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserServiceRoutingModule { }