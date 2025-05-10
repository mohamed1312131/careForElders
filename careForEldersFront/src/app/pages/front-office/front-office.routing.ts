import { Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { UserinfoComponent } from './user-service/userinfo/userinfo.component';
import {MedicalRecordComponent} from "./medical-record/medical-record/medical-record.component";
import { PatientBillFormComponent } from './patient-bill/patient-bill-form/patient-bill-form.component';
import {PlanListComponent} from "./nutrition/plan-list/plan-list.component";
import {PlanDetailsComponent} from "./nutrition/plan-details/plan-details.component";
import {AdminDashboardComponent} from "./nutrition/admin-dashboard/admin-dashboard.component";


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
    path:'medicalRecord',
    component:MedicalRecordComponent,
  },
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
  /*{
    path: 'bill',
    loadChildren: () =>
      import('./patient-bill/patient-bills.module').then((m) => m.PatientBillsModule),
    // Remove the component property here
  },

  {
    path: "blog-forum",
    loadChildren: () => import("./blog-forum/blog-forum.module").then((m) => m.BlogForumModule),
  }, */


];
