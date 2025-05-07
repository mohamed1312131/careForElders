import { Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';

export const FrontOfficeRoutes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        component: HomePageComponent,
      },

      {
        path: 'user',
        loadChildren: () =>
          import('./user-service/user-service.module').then((m) => m.UserModule),
      },
    ],
  },
];
