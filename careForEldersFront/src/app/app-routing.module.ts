import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BlankComponent } from './layouts/blank/blank.component';
import { FullComponent } from './layouts/full/full.component';
import { BillDetailsComponent } from './billing/billing/bill-details/bill-details.component';
import { BillListComponent } from './billing/billing/bill-list/bill-list.component';
import { BillCreateComponent } from './billing/billing/bill-create/bill-create.component';
import { BillEditComponent } from './billing/billing/bill-edit/bill-edit.component';


const routes: Routes = [
  // Public routes
  {
    path: '',
    loadChildren: () => import('./pages/front-office/front-office.module').then(m => m.FrontOfficeModule),
  },
  
  // Billing routes
  { path: 'bills', component: BillListComponent },
  { path: 'bills/create', component: BillCreateComponent },
  { path: 'bills/:id', component: BillDetailsComponent },
  { path: 'bills/:id/edit', component: BillEditComponent },
 
  // Admin auth routes (login/register)
  {
    path: 'admin',
    component: BlankComponent,
    children: [
      { path: '', redirectTo: 'authentication/login', pathMatch: 'full' },
      { path: 'authentication', loadChildren: () => import('./pages/authentication/authentication.module').then(m => m.AuthenticationModule) }
    ]
  },

  // Admin dashboard routes
  {
    path: 'admin',
    component: FullComponent,
    children: [
      { path: 'dashboard', loadChildren: () => import('./pages/pages.module').then(m => m.PagesModule) },
      { path: 'ui-components', loadChildren: () => import('./pages/ui-components/ui-components.module').then(m => m.UicomponentsModule) },
      { path: 'extra', loadChildren: () => import('./pages/extra/extra.module').then(m => m.ExtraModule) }
    ]
  },

  // Catch-all route
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}