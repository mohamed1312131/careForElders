import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BlankComponent } from './layouts/blank/blank.component';
import { FullComponent } from './layouts/full/full.component';
import { BillPaymentComponent } from './pages/billing/bill-payment/bill-payment.component';
import { BillDetailComponent } from './pages/billing/bill-detail/bill-detail.component';
import { BillListComponent } from './pages/billing/bill-list/bill-list.component';
import { BillCreateComponent } from './pages/billing/bill-create/bill-create.component';
import { InvoiceComponent } from './pages/billing/invoice/invoice.component';

const routes: Routes = [
  // Public routes
  {
    path: '',
    loadChildren: () => import('./pages/front-office/front-office.module').then(m => m.FrontOfficeModule),
  },
  
  // Billing routes
  {
    path: 'billing',
    children: [
      { path: 'create', component: BillCreateComponent },
      { path: 'bills', component: BillListComponent },
      { path: ':id', component: BillDetailComponent },
      { path: ':id/pay', component: BillPaymentComponent },
     { path: 'billing/invoice/:id',component: InvoiceComponent} 
    ]
  },
 
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