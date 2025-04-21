import { Routes } from '@angular/router';

import { LoginComponent} from './login/login.component';
import { RegisterComponent } from './register/register.component'; // ✅ corrected

export const AuthenticationRoutes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: 'register',
        component: RegisterComponent, // ✅ corrected
      },
    ],
  },
];
