import { Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { UserinfoComponent } from './user-service/userinfo/userinfo.component';
import {MedicalRecordComponent} from "./medical-record/medical-record/medical-record.component";
import { PatientBillFormComponent } from './patient-bill/patient-bill-form/patient-bill-form.component';


export const FrontOfficeRoutes: Routes = [
  {
    path: '',
    component: HomePageComponent, // Home page is now restored at the root
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
    path:'medicalRecord',
    component:MedicalRecordComponent,
  },
  {
    path: 'bill',
    loadChildren: () =>
      import('./patient-bill/patient-bills.module').then((m) => m.PatientBillsModule),
    // Remove the component property here
  },
  {
    path: "",
    redirectTo: "bill",
    pathMatch: "full",
  },

  
];
