import { Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { UsersComponent } from './user-service/user/user.component'; // Import the User component
import { UserinfoComponent } from './user-service/userinfo/userinfo.component';
import {MedicalRecordComponent} from "./medical-record/medical-record/medical-record.component";


export const FrontOfficeRoutes: Routes = [
  {
    path: '',
    component: HomePageComponent, // Home page is now restored at the root
  },
  {
    path: 'user', // Base path for user-related routes
    children: [
      {
        path: 'users',
        component: UsersComponent,
      },
      {
        path: 'userinfo/:id',
        component: UserinfoComponent,
      },
      {
        path: 'plan',
        loadChildren: () =>
          import('./plan-and-exercise/plan-and-exercise.module').then((m) => m.PlanAndExerciseModule),
      },
      {
        path: 'chat',
        loadChildren: () =>
          import('./chat/chat.module').then((m) => m.ChatModule),
      },
    ],

  },
  {
    path:'medicalRecord',
    component:MedicalRecordComponent,
  }
];
