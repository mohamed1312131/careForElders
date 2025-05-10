import { Component, OnInit } from '@angular/core';
import { NutritionService } from '../../../../services/nutrition.service';
import { NutritionPlan } from '../../../../models/nutrition-plan.model';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  plans: NutritionPlan[] = [];
  selectedPlan: NutritionPlan | null = null;
  isEditing = false;
  showForm = false;
  imagePreview: string | ArrayBuffer | null = null;
  ingredientsInput: string = '';

  newPlan: NutritionPlan = {
    id: '',
    meal: '',
    description: '',
    calories: 0,
    pictureUrl: '',
    mealTime: '',
    notes: '',
    recommendedAgeGroup: '',
    ingredients: [],      // ✅ New
    comments: [],         // ✅ New
    likes: 0,             // ✅ New
    dislikes: 0           // ✅ New
  };

  constructor(private nutritionService: NutritionService) { }

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans(): void {
    this.nutritionService.getAllPlans().subscribe({
      next: (plans) => this.plans = plans,
      error: (err) => console.error('Error fetching plans:', err)
    });
  }

  editPlan(plan: NutritionPlan): void {
    this.selectedPlan = { ...plan };
    this.ingredientsInput = (plan.ingredients || []).join(', '); // ← Split from array

    this.isEditing = true;
    this.showForm = true;
  }

  createNewPlan(): void {
    this.selectedPlan = null;
    this.newPlan = {
      id: '',
      meal: '',
      description: '',
      calories: 0,
      pictureUrl: '',
      mealTime: '',
      notes: '',
      recommendedAgeGroup: '',
      ingredients: [],      // ✅ New
      comments: [],         // ✅ New
      likes: 0,             // ✅ New
      dislikes: 0           // ✅ New
    };
    this.isEditing = false;
    this.showForm = true;
  }

  submitPlan(): void {
    const ingredientsArray = this.ingredientsInput
      ? this.ingredientsInput.split(',').map(ing => ing.trim()).filter(Boolean)
      : [];

    if (this.isEditing && this.selectedPlan) {
      this.selectedPlan.ingredients = ingredientsArray;
      this.nutritionService.updatePlan(this.selectedPlan.id, this.selectedPlan)
        .subscribe({
          next: () => {
            this.loadPlans();
            this.showForm = false;
          },
          error: (err) => console.error('Error updating plan:', err)
        });
    } else {
      this.newPlan.ingredients = ingredientsArray;
      this.nutritionService.createPlan(this.newPlan)
        .subscribe({
          next: () => {
            this.loadPlans();
            this.showForm = false;
          },
          error: (err) => console.error('Error creating plan:', err)
        });
    }
  }


  deletePlan(id: string): void {
    if (confirm('Are you sure you want to delete this plan?')) {
      this.nutritionService.deletePlan(id).subscribe({
        next: () => this.loadPlans(),
        error: (err) => console.error('Error deleting plan:', err)
      });
    }
  }

  cancelForm(): void {
    this.showForm = false;
  }
  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
        const url = reader.result as string;
        if (this.isEditing && this.selectedPlan) {
          this.selectedPlan.pictureUrl = url;
        } else {
          this.newPlan.pictureUrl = url;
        }
      };
      reader.readAsDataURL(file);
    }
  }

}
