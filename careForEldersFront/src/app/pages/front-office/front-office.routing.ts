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
];
