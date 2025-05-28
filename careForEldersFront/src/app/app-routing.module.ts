import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BlankComponent } from './layouts/blank/blank.component';
import { FullComponent } from './layouts/full/full.component';
import { MedicalRecordComponent } from './pages/front-office/medical-record/medical-record/medical-record.component';


const routes: Routes = [
  // Front office routes
  {
    path: '',
    loadChildren: () => import('./pages/front-office/front-office.module').then(m => m.FrontOfficeModule),
  },
  {
    path: 'authentication',
    loadChildren: () =>
    import('./pages/authentication/authentication.module').then((m) => m.AuthenticationModule),
  },

  // Admin routes
  {
    path: 'admin',
    component: FullComponent,
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./pages/pages.module').then((m) => m.PagesModule),
      },
      {
        path: 'ui-components',
        loadChildren: () =>
          import('./pages/ui-components/ui-components.module').then((m) => m.UicomponentsModule),
      },
      {
        path: 'extra',
        loadChildren: () =>
          import('./pages/extra/extra.module').then((m) => m.ExtraModule),
      },
      { path: 'medical-record', component: MedicalRecordComponent },
    ],
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],

})
export class AppRoutingModule {}
