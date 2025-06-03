import { Routes } from '@angular/router';

import { LoginComponent} from './login/login.component';
import { RegisterComponent } from './register/register.component';
import {PasswordResetComponent} from "./password-reset/password-reset.component";
import {ResetPasswordFormComponent} from "./reset-password-form/reset-password-form.component";
import {VerifyOtpComponent} from "./verify-otp/verify-otp.component";
import {OAuth2RedirectComponent} from "./OAuth2Redirect/OAuth2RedirectComponent"; // ✅ corrected
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
      },
      {
        path:'verify-otp',
        component:VerifyOtpComponent,
      },
      { path: 'oauth2/redirect', component: OAuth2RedirectComponent }

    ],
  },
];
