import { Routes } from '@angular/router';

import { AppSideLoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component'; // ✅ corrected

export const AuthenticationRoutes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'login',
        component: AppSideLoginComponent,
      },
      {
        path: 'register',
        component: RegisterComponent, // ✅ corrected
      },
    ],
  },
];
