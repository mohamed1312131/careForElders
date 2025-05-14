import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SubscriptionRoutingModule } from './subscription-routing.module';
import { AbonnementTypeComponent } from './abonnement-type/abonnement-type.component';


@NgModule({
  declarations: [AbonnementTypeComponent],
  imports: [
    CommonModule,
    SubscriptionRoutingModule
  ],
    exports:[AbonnementTypeComponent]
})
export class SubscriptionModule { }
