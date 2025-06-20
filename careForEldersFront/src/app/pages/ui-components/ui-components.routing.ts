import { Routes } from '@angular/router';

// ui
import { AppBadgeComponent } from './badge/badge.component';
import { AppChipsComponent } from './chips/chips.component';
import { AppListsComponent } from './lists/lists.component';
import { AppMenuComponent } from './menu/menu.component';
import { AppTooltipsComponent } from './tooltips/tooltips.component';
import { TestingComponent } from '../front-office/appointment-availability/testing/testing.component';
import { ReservationAdminComponent } from './reservation-admin/reservation-admin.component';
import { AbonnementAdminComponent } from './abonnement-admin/abonnement-admin.component';

export const UiComponentsRoutes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'badge',
        component: AppBadgeComponent,
      },
      {
        path: 'chips',
        component: AppChipsComponent,
      },
      {
        path: 'lists',
        component: AppListsComponent,
      },
      {
        path: 'menu',
        component: AppMenuComponent,
      },
      {
        path: 'tooltips',
        component: AppTooltipsComponent,
      },
      {path:'testing',
        component: TestingComponent
      },
      {path:'Reservation',
        component: ReservationAdminComponent
      },
      {path:'Abonnement',
        component: AbonnementAdminComponent
      }
    ],
  },
];
