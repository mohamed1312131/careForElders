import { Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { UserinfoComponent } from './user-service/userinfo/userinfo.component';
import {MedicalRecordComponent} from "./medical-record/medical-record/medical-record.component";
import { PatientBillFormComponent } from './patient-bill/patient-bill-form/patient-bill-form.component';

import {UsersComponent} from "./user-service/user/user.component";
import {MedicalRecordListComponent} from "./medical-record/medical-records-list/medical-records-list.component";
import { PostListComponent } from './blog-forum/post-list/post-list.component';


export const FrontOfficeRoutes: Routes = [
  {
    path: '',
    component: HomePageComponent, // Home page is now restored at the root
    pathMatch: 'full',
  },
  {
    path: 'user',
    loadChildren: () =>
      import('./user-service/user-service.module').then((m) => m.UserModule),
  },
  {
    path: 'plan',
    loadChildren: () =>
      import('./plan-and-exercise/plan-and-exercise.module').then((m) => m.PlanAndExerciseModule),
  },
  {
    path: 'chat',
    loadChildren: () => import('./chat/chat.module').then(m => m.ChatModule),
    data: { preload: true }
  },
  {
    path: 'appointement-availability',
    loadChildren: () => import('./appointment-availability/appointment-availability.module').then(m => m.AppointmentAvailabilityModule),
    data: { preload: true }
  },
  {
    path: 'paramedical-care',
    loadChildren: () => import('./paramedical-care/paramedical-care.module').then(m => m.ParamedicalCareModule),
    data: { preload: true }
  },


  {
    path: "post",
    component:PostListComponent,  },
  {
    path: 'abonnement',
    loadChildren: () =>
      import('./subscription/subscription.module').then((m) => m.SubscriptionModule),
  },

  {
    path: 'users',
    component: UsersComponent,
  },
  {
    path: 'userinfo/:id',
    component: UserinfoComponent,
  },
  {
    path:'medical-record/:id',
    component:MedicalRecordComponent,
  },
  {
    path:'medicalRecord',
    component:MedicalRecordComponent,},
  {
    path:'medicalRecordsList',
    component:MedicalRecordListComponent,
  },

  {
    path:'medicalRecord',
    component:MedicalRecordComponent,
  },
  {
    path: 'nutrition',
    loadChildren: () => import('./nutrition/nutrition.module').then(m => m.NutritionModule)
  },
  {
    path:'medicalrecordlist',
    component:MedicalRecordListComponent,
  },
  {
    path:'medicalrecord/:id',
    component:MedicalRecordComponent ,
  },
  /*{
    path: 'bill',
    loadChildren: () =>
      import('./patient-bill/patient-bills.module').then((m) => m.PatientBillsModule),
    // Remove the component property here
  },
*/



];
