import { Component, OnInit } from '@angular/core';
import { NutritionService } from '../../../../services/nutrition.service';
import { NutritionPlan } from '../../../../models/nutrition-plan.model';
import { Router } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-plan-list',
  templateUrl: './plan-list.component.html',
  styleUrls: ['./plan-list.component.scss']
})
export class PlanListComponent implements OnInit {
  plans: NutritionPlan[] = [];
  searchTerm: string = '';
  loading: boolean = false;

  // Pagination properties
  totalPlans: number = 0;
  pageSize: number = 10;
  currentPage: number = 0;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  // Sorting properties (optional, add if needed)
  sortBy: string = 'createdAt'; // Default sort

  constructor(private nutritionService: NutritionService, private router: Router) { }

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans(): void {
    this.loading = true;
    if (this.searchTerm.trim()) {
      this.nutritionService.searchPlans(this.searchTerm, this.currentPage, this.pageSize).subscribe({
        next: (plans) => {
          this.plans = plans;
          // Assuming the backend doesn't return total count in search, might need another call or backend change
          // For simplicity, we'll reset totalPlans based on results length, but ideally backend provides total
          this.totalPlans = plans.length; // This is incorrect for pagination, needs backend support
          this.loading = false;
          console.warn('Pagination might not work correctly with search without total count from backend.');
        },
        error: (err) => {
          console.error('Error searching plans:', err);
          this.loading = false;
        }
      });
    } else {
      this.nutritionService.getAllPlans(this.currentPage, this.pageSize, this.sortBy).subscribe({
        next: (plans) => {
          this.plans = plans;
          // Ideally, the backend API for getAllPlans should return total count
          // Let's assume for now it doesn't, and we need a separate call or estimate
          // We might need to call getStats() or have a dedicated count endpoint
          // For now, setting a placeholder value or estimating
          this.fetchTotalPlansCount(); // Call a method to get the total count
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching plans:', err);
          this.loading = false;
        }
      });
    }
  }

  fetchTotalPlansCount(): void {
    // This is a placeholder. Ideally, getAllPlans returns total count or use getStats
    this.nutritionService.getStats().subscribe({
      next: (stats: any) => {
        // Assuming stats map contains 'totalPlans'
        const total = stats['totalPlans'];
        this.totalPlans = total !== undefined ? Number(total) : this.plans.length; // Fallback if key not found
      },
      error: (err) => {
        console.error('Error fetching stats for total count:', err);
        this.totalPlans = this.plans.length; // Fallback on error
      }
    });
  }

  onSearchTermChange(): void {
    this.currentPage = 0; // Reset to first page on new search
    this.loadPlans();
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.currentPage = 0;
    this.loadPlans();
  }

  handlePageEvent(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadPlans();
  }

  viewPlanDetails(id: string): void {
    if (!id) { // Vérifiez si l'ID est valide
      console.error('Erreur : ID invalide reçu pour la navigation.', id);
      return;
    }

    // Utilisez navigate pour gérer dynamiquement les paramètres de route
    this.router.navigate(['nutritionplandetails/', id])
      .then(success => {
        if (success) {
          console.log(`Navigation réussie avec l'ID : ${id}`);
        } else {
          console.error('La navigation a échoué.');
        }
      })
      .catch(err => console.error('Erreur lors de la navigation :', err));

    window.scrollTo(0, 0); // Gardez cette ligne pour faire défiler en haut de page
  }

  likePlan(id: string, event: Event): void {
    event.stopPropagation(); // Prevent triggering row click
    this.nutritionService.likePlan(id).subscribe({
      next: (updatedPlan) => {
        // Update the specific plan in the local array
        const index = this.plans.findIndex(p => p.id === id);
        if (index !== -1) {
          this.plans[index] = updatedPlan;
        }
      },
      error: (err) => console.error('Error liking plan:', err)
    });
  }

  dislikePlan(id: string, event: Event): void {
    event.stopPropagation(); // Prevent triggering row click
    this.nutritionService.dislikePlan(id).subscribe({
      next: (updatedPlan) => {
        // Update the specific plan in the local array
        const index = this.plans.findIndex(p => p.id === id);
        if (index !== -1) {
          this.plans[index] = updatedPlan;
        }
      },
      error: (err) => console.error('Error disliking plan:', err)
    });
  }

  // Add methods for other actions if needed (e.g., delete, navigate to create)
  deletePlan(id: string, event: Event): void {
    event.stopPropagation();
    if(confirm('Are you sure you want to delete this plan?')) {
      this.nutritionService.deletePlan(id).subscribe({
        next: () => {
          console.log(`Plan ${id} deleted`);
          // Remove plan from local array or reload plans
          this.plans = this.plans.filter(p => p.id !== id);
          this.totalPlans--; // Decrement total count
        },
        error: (err) => console.error('Error deleting plan:', err)
      });
    }
  }

  navigateToCreate(): void {
    // Assuming a route like '/front-office/nutrition/create'
    this.router.navigate(['/front-office/nutrition/create']);
  }
}
