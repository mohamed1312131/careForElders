import { Component, OnInit } from '@angular/core';
import { NutritionPlan } from 'src/app/models/nutrition-plan.model';
import { NutritionService } from '../../../../services/nutrition.service';
//import { NutritionPlan } from '../../../../models/nutrition-plan.model';
import { MealSchedule } from 'src/app/models/meal-schedule.model';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent  implements OnInit {
  
  plans: NutritionPlan[] = [];
  selectedPlan: NutritionPlan | null = null;
  isEditing = false;
  showForm = false;

  // Propriétés d'édition pour éviter les problèmes de nullité dans le template
  editableUserId: string = '';
  editableUserEmail: string = '';
  editableMedicalConditions: string = '';
  editableDietaryPreferences: string = '';
  editableAllergies: string = '';
  editablePlanDuration: number = 30;
  editableTargetCalories: number = 2000;
  editableActive: boolean = true;
  editableEmailRemindersEnabled: boolean = true;
  editableAiGeneratedPlan: string = '';

  // Propriétés pour l'horaire des repas
  editableBreakfastTime: string = '08:00';
  editableLunchTime: string = '13:00';
  editableDinnerTime: string = '19:00';
  editableSnackTimes: string[] = [];

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
    this.isEditing = true;
    this.showForm = true; 

    // Initialiser les propriétés d'édition avec les valeurs du plan
    this.editableUserId = plan.userId || '';
    this.editableUserEmail = plan.userEmail || '';
    this.editableMedicalConditions = plan.medicalConditions || '';
    this.editableDietaryPreferences = plan.dietaryPreferences || '';
    this.editableAllergies = plan.allergies || '';
    this.editablePlanDuration = plan.planDuration || 30;
    this.editableTargetCalories = plan.targetCalories || 2000;
    this.editableActive = plan.active !== undefined ? plan.active : true;
    this.editableEmailRemindersEnabled = plan.emailRemindersEnabled !== undefined ? plan.emailRemindersEnabled : true;
    this.editableAiGeneratedPlan = plan.aiGeneratedPlan || '';

    // Initialiser les propriétés d'horaire des repas
    if (plan.mealSchedule) {
      this.editableBreakfastTime = plan.mealSchedule.breakfastTime || '08:00';
      this.editableLunchTime = plan.mealSchedule.lunchTime || '13:00';
      this.editableDinnerTime = plan.mealSchedule.dinnerTime || '19:00';
      this.editableSnackTimes = plan.mealSchedule.snackTimes ? [...plan.mealSchedule.snackTimes] : [];
    } else {
      this.resetMealSchedule();
    }
  }

  createNewPlan(): void {
    this.selectedPlan = null;
    this.isEditing = false;
    this.showForm = true;

    // Réinitialiser les propriétés d'édition
    this.editableUserId = '';
    this.editableUserEmail = '';
    this.editableMedicalConditions = '';
    this.editableDietaryPreferences = '';
    this.editableAllergies = '';
    this.editablePlanDuration = 30;
    this.editableTargetCalories = 2000;
    this.editableActive = true;
    this.editableEmailRemindersEnabled = true;
    this.editableAiGeneratedPlan = '';

    // Réinitialiser l'horaire des repas
    this.resetMealSchedule();
  }

  resetMealSchedule(): void {
    this.editableBreakfastTime = '08:00';
    this.editableLunchTime = '13:00';
    this.editableDinnerTime = '19:00';
    this.editableSnackTimes = [];
  }

  submitPlan(): void {
    // Créer un objet MealSchedule à partir des propriétés éditables
    const mealSchedule: MealSchedule = {
      breakfastTime: this.editableBreakfastTime,
      lunchTime: this.editableLunchTime,
      dinnerTime: this.editableDinnerTime,
      snackTimes: [...this.editableSnackTimes]
    };

    if (this.isEditing && this.selectedPlan) {
      // Mettre à jour le plan existant avec les valeurs éditables
      const updatedPlan: NutritionPlan = {
        ...this.selectedPlan,
        userId: this.editableUserId,
        userEmail: this.editableUserEmail,
        medicalConditions: this.editableMedicalConditions,
        dietaryPreferences: this.editableDietaryPreferences,
        allergies: this.editableAllergies,
        planDuration: this.editablePlanDuration,
        targetCalories: this.editableTargetCalories,
        active: this.editableActive,
        emailRemindersEnabled: this.editableEmailRemindersEnabled,
        aiGeneratedPlan: this.editableAiGeneratedPlan,
        mealSchedule: mealSchedule
      };

      this.nutritionService.updatePlan(updatedPlan.id, updatedPlan)
        .subscribe({
          next: () => {
            this.loadPlans();
            this.showForm = false;
          },
          error: (err) => console.error('Error updating plan:', err)
        });
    } else {
      // Créer un nouveau plan avec les valeurs éditables
      const newPlan: NutritionPlan = {
        id: '',
        userId: this.editableUserId,
        userEmail: this.editableUserEmail,
        medicalConditions: this.editableMedicalConditions,
        dietaryPreferences: this.editableDietaryPreferences,
        allergies: this.editableAllergies,
        planDuration: this.editablePlanDuration,
        targetCalories: this.editableTargetCalories,
        active: this.editableActive,
        emailRemindersEnabled: this.editableEmailRemindersEnabled,
        aiGeneratedPlan: this.editableAiGeneratedPlan,
        createdAt: new Date(),
        updatedAt: new Date(),
        likes: 0,
        dislikes: 0,
        comments: [],
        mealSchedule: mealSchedule
      };

      this.nutritionService.createPlan(newPlan)
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

  // Méthode pour ajouter un horaire de collation
  addSnackTime(): void {
    this.editableSnackTimes.push('15:00');
  }

  // Méthode pour supprimer un horaire de collation
  removeSnackTime(index: number): void {
    this.editableSnackTimes.splice(index, 1);
  }

  // Fonction TrackBy pour ngFor des heures de collation
  trackBySnackTime(index: number): number {
    return index;
  }

  // Méthode pour générer un plan via IA
  generatePlanWithAI(): void {
    if (!this.editableUserId || !this.editableUserEmail || !this.editableMedicalConditions) {
      alert('User ID, email and medical conditions are required for AI generation');
      return;
    }

    this.nutritionService.generateMonthlyPlan(
      this.editableUserId,
      this.editableMedicalConditions,
      this.editableUserEmail
    ).subscribe({
      next: (generatedPlan) => {
        this.loadPlans();
        this.showForm = false;
        alert('Plan generated successfully!');
      },
      error: (err) => {
        console.error('Error generating plan:', err);
        alert('Failed to generate plan. Please try again.');
      }
    });
  }
}
