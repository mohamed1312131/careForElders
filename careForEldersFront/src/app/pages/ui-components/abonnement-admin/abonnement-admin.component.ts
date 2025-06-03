import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
//import { SubscriptionService, SubscriptionPlan, UserSubscription, SubscriptionAnalytics } from './subscription.service';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { SubscriptionAnalytics, SubscriptionPlan, SubscriptionPlanDTO, SubscriptionService, UserSubscription, UserSubscriptionDTO } from '../subscription.service';

@Component({
  selector: 'app-abonnement-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './abonnement-admin.component.html',
  styleUrls: ['./abonnement-admin.component.scss'],
  // Add this if you're not using providedIn: 'root' in the service
  providers: [SubscriptionService] 
})
export class AbonnementAdminComponent implements OnInit {
  adminId = '6831b1f8f67d19063be85851';
  showAddPlanModal = false;
  showUserDetailsModal = false;
  
  // Data from backend
  analytics: SubscriptionAnalytics | null = null;
  plans: SubscriptionPlan[] = [];
  userSubscriptions: UserSubscription[] = [];
  searchTerm = '';
  
  // Form models
  newPlan: Omit<SubscriptionPlan, 'id'> = {
    name: '',
    price: 0,
    durationDays: 30,  // Changed from duration to durationDays to match interface
    features: []
  };
  
  selectedUser: UserSubscription | null = null;
  
  private subscriptions = new Subscription();

  constructor(private subscriptionService: SubscriptionService) {}

  ngOnInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadData(): void {
    // Load analytics
    this.subscriptions.add(
      this.subscriptionService.getAnalytics(this.adminId).subscribe((data: SubscriptionAnalytics) => {
        this.analytics = data;
      })
    );

    // Load plans
    this.subscriptions.add(
      this.subscriptionService.getAllPlans().subscribe((plans: SubscriptionPlan[]) => {
        this.plans = plans;
      })
    );

    // Load user subscriptions
    this.loadUserSubscriptions();
  }

  loadUserSubscriptions(): void {
    this.subscriptions.add(
      this.subscriptionService.getSubscriptionsByUser(this.searchTerm).subscribe((users: UserSubscription[]) => {
        this.userSubscriptions = users;
      })
    );
  }

  // Plan Management
  openAddPlanModal(): void {
    this.showAddPlanModal = true;
    this.newPlan = { name: '', price: 0, durationDays: 30, features: [] };
  }

  closeAddPlanModal(): void {
    this.showAddPlanModal = false;
  }

  // In your component class
addPlan(): void {
  if (!this.adminId || !this.newPlan.name || !this.newPlan.price) {
    alert('Please fill all required fields');
    return;
  }

  const planDto: SubscriptionPlanDTO = {
    name: this.newPlan.name,
    price: this.newPlan.price,
    durationDays: this.newPlan.durationDays,
    features: this.newPlan.features
  };

  this.subscriptionService.createPlan(this.adminId, planDto).subscribe({
    next: (createdPlan: SubscriptionPlan) => {
      // Handle successful creation
      this.plans.push(createdPlan);
      this.closeAddPlanModal();
      this.loadData(); // Refresh your data
      alert('Plan created successfully!');
    },
    error: (err) => {
      console.error('Error creating plan:', err);
      alert('Failed to create plan. Please try again.');
    }
  });
}

  updatePlan(plan: SubscriptionPlan): void {
    const planDto: SubscriptionPlanDTO = {
      name: plan.name,
      price: plan.price,
      durationDays: plan.durationDays,
      features: plan.features
    };
    
    this.subscriptions.add(
      this.subscriptionService.updatePlan(this.adminId, plan.id, planDto).subscribe((updatedPlan: SubscriptionPlan) => {
        const index = this.plans.findIndex(p => p.id === updatedPlan.id);
        if (index !== -1) {
          this.plans[index] = updatedPlan;
        }
      })
    );
  }

  deletePlan(planId: string): void {
    if (confirm('Are you sure you want to delete this plan?')) {
      this.subscriptions.add(
        this.subscriptionService.deletePlan(this.adminId, planId).subscribe(() => {
          this.plans = this.plans.filter(plan => plan.id !== planId);
          this.loadData(); // Refresh analytics
        })
      );
    }
  }

  // User Subscription Management
  openUserDetailsModal(user: UserSubscription): void {
    this.selectedUser = user;
    this.showUserDetailsModal = true;
  }

  closeUserDetailsModal(): void {
    this.showUserDetailsModal = false;
    this.selectedUser = null;
  }

  changeUserPlan(userId: string, planId: string): void {
    const dto: UserSubscriptionDTO = {
      userId: userId,
      planId: planId
    };
    
    this.subscriptions.add(
      this.subscriptionService.assignPlanToUser(dto).subscribe((updatedSubscription: UserSubscription) => {
        const index = this.userSubscriptions.findIndex(u => u.userId === updatedSubscription.userId);
        if (index !== -1) {
          this.userSubscriptions[index] = updatedSubscription;
        }
        if (this.selectedUser?.userId === updatedSubscription.userId) {
          this.selectedUser = updatedSubscription;
        }
      })
    );
  }

  searchUsers(): void {
    this.loadUserSubscriptions();
  }

  // Helper function to convert comma-separated features to array
  updateFeatures(featuresString: string): void {
    this.newPlan['features'] = featuresString.split(',').map(f => f.trim()).filter(f => f);
  }
}