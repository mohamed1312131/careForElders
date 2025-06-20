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
    userId: '',
    userEmail: '',
    medicalConditions: '',
    dietaryPreferences: '',
    allergies: '',
    aiGeneratedPlan: '',
    content: '',
    createdAt: '',
    updatedAt: '',
    lastReminderSent: '',
    active: true,
    emailRemindersEnabled: true,
    meal: '',
    description: '',
    calories: 0,
    mealTime: '',
    notes: '',
    recommendedAgeGroup: '',
    pictureUrl: '',
    ingredients: [],
    planDuration: 30,
    targetCalories: 2000,
    mealSchedule: null,
    comments: [],
    likes: 0,
    dislikes: 0,
    localImage: null,
    imagePreview: null
  };


  constructor(private nutritionService: NutritionService) { }

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans(): void {
    this.nutritionService.getAllPlans().subscribe({
      next: (response: any) => {
        console.log('Loaded plans response:', response);
        let plans: any[] = [];
        if (Array.isArray(response)) {
          plans = response;
        } else if (response && Array.isArray((response as any).content)) {
          plans = (response as any).content;
        }
        // Map and fill missing fields with defaults
        this.plans = plans.map(plan => ({
          ...plan,
          meal: plan.meal || '(No Meal Name)',
          description: plan.description || '',
          calories: plan.calories ?? 0,
          mealTime: plan.mealTime || '',
          notes: plan.notes || '',
          recommendedAgeGroup: plan.recommendedAgeGroup || '',
          pictureUrl: plan.pictureUrl || '',
          ingredients: plan.ingredients || [],
          userId: plan.userId || '',
          userEmail: plan.userEmail || '',
          medicalConditions: plan.medicalConditions || '',
          dietaryPreferences: plan.dietaryPreferences || '',
          allergies: plan.allergies || '',
          aiGeneratedPlan: plan.aiGeneratedPlan || '',
          content: plan.content || '',
          createdAt: plan.createdAt || '',
          updatedAt: plan.updatedAt || '',
          lastReminderSent: plan.lastReminderSent || '',
          planDuration: plan.planDuration ?? 30,
          targetCalories: plan.targetCalories ?? 2000,
          mealSchedule: plan.mealSchedule || null,
          emailRemindersEnabled: plan.emailRemindersEnabled ?? true,
          comments: Array.isArray(plan.comments) ? plan.comments : [],
          likes: plan.likes ?? 0,
          dislikes: plan.dislikes ?? 0,
          active: plan.active ?? true
        }));
      },
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
      userId: '',
      userEmail: '',
      medicalConditions: '',
      dietaryPreferences: '',
      allergies: '',
      aiGeneratedPlan: '',
      content: '',
      createdAt: '',
      updatedAt: '',
      lastReminderSent: '',
      active: true,
      emailRemindersEnabled: true,
      meal: '',
      description: '',
      calories: 0,
      mealTime: '',
      notes: '',
      recommendedAgeGroup: '',
      pictureUrl: '',
      ingredients: [],
      planDuration: 30,
      targetCalories: 2000,
      mealSchedule: null,
      comments: [],
      likes: 0,
      dislikes: 0,
      localImage: null,
      imagePreview: null
    };
    this.isEditing = false;
    this.showForm = true;
  }

  isSubmitting = false;
  submitPlan(): void {
    this.isSubmitting = true;
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
            this.isSubmitting = false;
            alert('Plan updated successfully!');
          },
          error: (err) => {
            this.isSubmitting = false;
            alert('Error updating plan: ' + (err?.message || err));
          }
        });
    } else {
      this.newPlan.ingredients = ingredientsArray;
      this.newPlan.active = true; // Always set active to true
      this.nutritionService.createPlan(this.newPlan)
        .subscribe({
          next: () => {
            this.loadPlans();
            this.showForm = false;
            this.isSubmitting = false;
            alert('Plan created successfully!');
          },
          error: (err) => {
            this.isSubmitting = false;
            alert('Error creating plan: ' + (err?.message || err));
          }
        });
    }
  }


  deletePlan(id: string): void {
    console.log('Attempting to delete plan with ID:', id);
    if (!id) {
      alert('Cannot delete: Plan ID is missing or invalid.');
      return;
    }
    if (confirm('Are you sure you want to delete this plan?')) {
      this.nutritionService.deletePlan(id).subscribe({
        next: () => this.loadPlans(),
        error: (err) => {
          alert('Error deleting plan: ' + (err?.message || err));
          console.error('Error deleting plan:', err);
        }
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
