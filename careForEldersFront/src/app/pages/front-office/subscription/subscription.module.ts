import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SubscriptionRoutingModule } from './subscription-routing.module';
import { AbonnementTypeComponent } from './abonnement-type/abonnement-type.component';
import { UnauthorizedComponent } from './unauthorized/unauthorized.component';


@NgModule({
  declarations: [AbonnementTypeComponent,
    UnauthorizedComponent,
  ],
  imports: [
    CommonModule,
    SubscriptionRoutingModule
  ],
    exports:[AbonnementTypeComponent]
})
export class SubscriptionModule { }
