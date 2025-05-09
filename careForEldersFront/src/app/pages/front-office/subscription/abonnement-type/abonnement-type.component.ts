import { Component, OnInit } from '@angular/core';
import { AbonnementService } from '../service-abonnement/abonnement.service';

@Component({
  selector: 'app-abonnement-type',
  templateUrl: './abonnement-type.component.html',
  styleUrl: './abonnement-type.component.scss'
})
export class AbonnementTypeComponent implements OnInit {
  subscriptionPlans: any[] = [];
  currentSubscription: any = null;
  loading = true;
  error: string | null = null;

  constructor(private subscriptionService: AbonnementService) {}

  ngOnInit(): void {
    this.loadCurrentSubscription();
    this.loadSubscriptionPlans();
  }


  loadCurrentSubscription(): void {
    // In a real app, you'd get userId from auth service
    const userId = '681d4e2c83d11505d70b9b6e';
    this.subscriptionService.getCurrentSubscription(userId).subscribe({
      next: (subscription) => {
        this.currentSubscription = subscription;
      },
      error: (err) => {
        console.error('Failed to load current subscription', err);
        // You might want to handle this error differently since it's not critical
      }
    });
  }


  loadSubscriptionPlans(): void {
    this.subscriptionService.getSubscriptionPlans().subscribe({
      next: (plans) => {
        this.subscriptionPlans = plans;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load subscription plans';
        this.loading = false;
        console.error(err);
      }
    });
  }

  subscribe(planId: string): void {
    // In a real app, you'd get userId from auth service
    const userId = 'user_123'; 
    this.subscriptionService.subscribeUser(userId, planId).subscribe({
      next: (response) => {
        alert('Subscription successful!');
        // Handle successful subscription
      },
      error: (err) => {
        alert('Subscription failed');
        console.error(err);
      }
    });
  }

}
