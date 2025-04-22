import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BlankComponent } from './layouts/blank/blank.component';
import { FullComponent } from './layouts/full/full.component';
import {PasswordResetComponent} from "./pages/authentication/password-reset/password-reset.component";
import {ResetPasswordFormComponent} from "./pages/authentication/reset-password-form/reset-password-form.component";

const routes: Routes = [
  // Front office routes
  {
    path: '',
    loadChildren: () =>
      import('./pages/front-office/front-office.module').then((m) => m.FrontOfficeModule),
  },

  // Consolidated Admin routes
  {
    path: 'admin',
    component: BlankComponent,  // Main admin layout
    children: [
      {
        path: '',
        redirectTo: 'authentication/login',
        pathMatch: 'full',
      },
      {
        path: 'authentication',
        loadChildren: () =>
          import('./pages/authentication/authentication.module').then((m) => m.AuthenticationModule),
      },
      {
        path: 'dashboard',
        component: FullComponent,  // Nested layout for dashboard
        loadChildren: () =>
          import('./pages/pages.module').then((m) => m.PagesModule),
      },
      {
        path: 'ui-components',
        component: FullComponent,
        loadChildren: () =>
          import('./pages/ui-components/ui-components.module').then((m) => m.UicomponentsModule),
      },
      {
        path: 'extra',
        component: FullComponent,
        loadChildren: () =>
          import('./pages/extra/extra.module').then((m) => m.ExtraModule),
      },
    ],
  },

  // Wildcard route
  {
    path: '**',
    redirectTo: '',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],

})
export class AppRoutingModule {}
