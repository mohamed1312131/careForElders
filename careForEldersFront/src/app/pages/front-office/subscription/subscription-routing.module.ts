import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
<<<<<<< Updated upstream

const routes: Routes = [];
=======
import { AbonnementTypeComponent } from './abonnement-type/abonnement-type.component';
import { UnauthorizedComponent } from './unauthorized/unauthorized.component';

const routes: Routes = [
 {path:'Abonnement', component:AbonnementTypeComponent},
  { path: 'unauthorized', component: UnauthorizedComponent },
];
>>>>>>> Stashed changes

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SubscriptionRoutingModule { }
