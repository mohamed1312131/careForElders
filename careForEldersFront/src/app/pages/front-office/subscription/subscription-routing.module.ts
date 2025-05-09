import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AbonnementTypeComponent } from './abonnement-type/abonnement-type.component';

const routes: Routes = [
 {path:'Abonnement', component:AbonnementTypeComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SubscriptionRoutingModule { }
