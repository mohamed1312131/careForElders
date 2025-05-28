import { Component, OnInit } from '@angular/core';
import { AbonnementService } from '../service-abonnement/abonnement.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-abonnement-type',
  templateUrl: './abonnement-type.component.html',
  styleUrls: ['./abonnement-type.component.scss'],
  providers: [DatePipe]
})
export class AbonnementTypeComponent implements OnInit {
  subscriptionPlans: any[] = [];
  currentSubscription: any = null;
  loading = true;
  error: string | null = null;
  userId: string | null = null;

  constructor(
    private subscriptionService: AbonnementService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.getUserInfo();
    if (this.userId) {
      this.loadCurrentSubscription();
      this.loadSubscriptionPlans();
    }
  }

  getUserInfo(): void {
    this.userId = localStorage.getItem('user_id');
    console.log('User ID:', this.userId);
    if (!this.userId) {
      console.warn('User ID not found in local storage.');
      this.error = 'User not authenticated. Please log in.';
      this.loading = false;
    }
  }

  loadCurrentSubscription(): void {
    if (!this.userId) return;
    
    this.subscriptionService.getCurrentSubscription(this.userId).subscribe({
      next: (subscription) => {
        this.currentSubscription = subscription;
        // Format dates for display
        if (this.currentSubscription) {
          this.currentSubscription.formattedStartDate = this.formatDate(this.currentSubscription.startDate);
          this.currentSubscription.formattedEndDate = this.formatDate(this.currentSubscription.endDate);
        }
         if (this.currentSubscription) {
    const endDate = new Date(this.currentSubscription.endDate);
    const today = new Date();

    const diffInMs = endDate.getTime() - today.getTime();
    this.currentSubscription.remainingDays = Math.max(
      Math.ceil(diffInMs / (1000 * 60 * 60 * 24)),
      0
    );
  }
      },
      error: (err) => {
        console.error('Failed to load current subscription', err);
        this.error = 'Failed to load current subscription';
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
    if (!this.userId) {
      console.warn('Cannot subscribe - no user ID available');
      this.error = 'User not authenticated. Please log in.';
      return;
    }
    
    this.subscriptionService.subscribeUser(this.userId, planId).subscribe({
      next: (response) => {
        alert('Subscription successful!');
        this.loadCurrentSubscription(); // Refresh current subscription
      },
      error: (err) => {
        alert('Subscription failed');
        console.error(err);
      }
    });
  }

  private formatDate(dateString: string): string {
    return this.datePipe.transform(dateString, 'mediumDate') || '';
  }
}