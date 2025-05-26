import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AbonnementTypeComponent } from './abonnement-type/abonnement-type.component';
import { UnauthorizedComponent } from './unauthorized/unauthorized.component';

const routes: Routes = [
 {path:'Abonnement', component:AbonnementTypeComponent},
  { path: 'unauthorized', component: UnauthorizedComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SubscriptionRoutingModule { }
