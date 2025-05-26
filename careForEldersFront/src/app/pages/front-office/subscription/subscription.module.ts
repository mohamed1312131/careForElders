import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SubscriptionRoutingModule } from './subscription-routing.module';
<<<<<<< Updated upstream


@NgModule({
  declarations: [],
=======
import { AbonnementTypeComponent } from './abonnement-type/abonnement-type.component';
import { UnauthorizedComponent } from './unauthorized/unauthorized.component';


@NgModule({
  declarations: [AbonnementTypeComponent,
    UnauthorizedComponent,
  ],
>>>>>>> Stashed changes
  imports: [
    CommonModule,
    SubscriptionRoutingModule
  ]
})
export class SubscriptionModule { }
