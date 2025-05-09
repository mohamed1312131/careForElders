import { Component } from '@angular/core';

@Component({
  selector: 'app-abonnement-type',
  templateUrl: './abonnement-type.component.html',
  styleUrl: './abonnement-type.component.scss'
})
export class AbonnementTypeComponent {
   abonnementTypes = [
    { name: 'Basic', price: '10$', duration: '1 Month' },
    { name: 'Standard', price: '25$', duration: '3 Months' },
    { name: 'Premium', price: '80$', duration: '12 Months' }
  ];

}
