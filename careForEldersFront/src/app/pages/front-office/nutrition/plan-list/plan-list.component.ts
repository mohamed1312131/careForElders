import { Component, OnInit } from '@angular/core';
import { NutritionService } from '../../../../services/nutrition.service';
import { NutritionPlan } from '../../../../models/nutrition-plan.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-plan-list',
  templateUrl: './plan-list.component.html',
  styleUrls: ['./plan-list.component.scss']
})
export class PlanListComponent implements OnInit {
  plans: NutritionPlan[] = [];
  filteredPlans: NutritionPlan[] = [];
  searchTerm: string = '';

  constructor(private nutritionService: NutritionService,  private router: Router) { }

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans(): void {
    this.nutritionService.getAllPlans().subscribe({
      next: (plans) => {
        console.log('Fetched plans:', plans); // Debug log
        this.plans = plans;
        this.filteredPlans = [...plans];
      },
      error: (err) => console.error('Error fetching plans:', err)
    });
  }

  filterPlans(): void {
    if (!this.searchTerm) {
      this.filteredPlans = [...this.plans];
      return;
    }
    this.filteredPlans = this.plans.filter(plan =>
      plan.meal.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      plan.description.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }
  viewPlanDetails(id: string): void {
    this.router.navigate(['/nutritionplandetails', id]);

    // Optional: Scroll to top after navigation
    window.scrollTo(0, 0);
  }
  likePlan(id: string, event: Event): void {
    event.stopPropagation();
    this.nutritionService.likePlan(id).subscribe({
      next: (updatedPlan) => {
        this.plans = this.plans.map(p => p.id === id ? updatedPlan : p);
      },
      error: (err) => console.error('Error liking plan:', err)
    });
  }

  dislikePlan(id: string, event: Event): void {
    event.stopPropagation();
    this.nutritionService.dislikePlan(id).subscribe({
      next: (updatedPlan) => {
        this.plans = this.plans.map(p => p.id === id ? updatedPlan : p);
      },
      error: (err) => console.error('Error disliking plan:', err)
    });
  }
}
