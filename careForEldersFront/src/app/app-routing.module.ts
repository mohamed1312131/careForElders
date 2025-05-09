import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BlankComponent } from './layouts/blank/blank.component';
import { FullComponent } from './layouts/full/full.component';
import { EventsListComponent } from './pages/event/events-list/events-list.component';
import { EventRegistrationComponent } from './pages/event/event-registration/event-registration.component';


const routes: Routes = [
  // Front office routes
  {
    path: '',
    loadChildren: () => import('./pages/front-office/front-office.module').then(m => m.FrontOfficeModule),
  },
  {
    path: 'events',
    component: EventRegistrationComponent,
    data: { title: 'Events' }
  },

  // Admin routes
  {
    path: 'admin',
    component: BlankComponent,
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
    ],
  },
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
        path: 'events',  // This will be /admin/events
        component: EventsListComponent,
        data: { title: 'Events Management' }
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
    ],
  },

  // Wildcard route for a 404 page
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