import { Routes } from '@angular/router';

import { LoginComponent} from './login/login.component';
import { RegisterComponent } from './register/register.component';
import {PasswordResetComponent} from "./password-reset/password-reset.component";
import {ResetPasswordFormComponent} from "./reset-password-form/reset-password-form.component"; // ✅ corrected
// authentication.routing.ts
export const AuthenticationRoutes: Routes = [
  {
    path: '',  // This is relative to 'admin/authentication'
    children: [
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: 'register',
        component: RegisterComponent,
      },
      {
        path: 'forgot-password',
        component: PasswordResetComponent
      },
      {
        path: 'reset-password',
        component: ResetPasswordFormComponent
      }
    ],
  },
];
