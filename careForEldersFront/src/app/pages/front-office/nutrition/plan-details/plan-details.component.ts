import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NutritionService } from '../../../../services/nutrition.service';
import { NutritionPlan } from '../../../../models/nutrition-plan.model';
import { Comment } from '../../../../models/comment.model';
import { MealSchedule } from '../../../../models/meal-schedule.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-plan-details',
  templateUrl: './plan-details.component.html',
  styleUrls: ['./plan-details.component.scss']
})
export class PlanDetailsComponent implements OnInit {
  plan: NutritionPlan | null = null;
  comments: Comment[] = [];
  loading = true;
  loadingComments = false;
  showCommentForm = false;
  newCommentText = '';
  isSubmittingComment = false;
  commentError = '';

  // Pour l'édition de l'horaire des repas (optionnel, exemple basique)
  editingSchedule = false;
  editableSchedule: MealSchedule = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private nutritionService: NutritionService,
    private snackBar: MatSnackBar,
    private cdRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadPlanDetails(id);
      this.loadComments(id);
    }
  }

  loadPlanDetails(id: string): void {
    this.loading = true;
    this.nutritionService.getPlanById(id).subscribe({
      next: (plan) => {
        this.plan = plan;
        // Initialiser editableSchedule si le plan a un horaire
        this.editableSchedule = this.plan.mealSchedule ? { ...this.plan.mealSchedule } : this.getDefaultSchedule();
        this.loading = false;
        this.cdRef.detectChanges(); // Déclencher la détection de changement
      },
      error: (err) => {
        console.error('Error fetching plan:', err);
        this.snackBar.open('Erreur lors du chargement des détails du plan.', 'Fermer', { duration: 3000 });
        this.loading = false;
        this.cdRef.detectChanges(); // Déclencher la détection de changement
      }
    });
  }

  loadComments(id: string): void {
    this.loadingComments = true;
    this.nutritionService.getComments(id).subscribe({
      next: (comment) => {
       // this.comments = comments;
        this.loadingComments = false;
        this.cdRef.detectChanges(); // Déclencher la détection de changement
      },
      error: (err) => {
        console.error('Error fetching comments:', err);
        // Gérer le cas où les commentaires pourraient être vides ou l'endpoint échoue
        this.comments = [];
        this.loadingComments = false;
        this.cdRef.detectChanges(); // Déclencher la détection de changement
      }
    });
  }

  likePlan(): void {
    if (!this.plan) return;
    this.nutritionService.likePlan(this.plan.id).subscribe({
      next: (updatedPlan) => {
        this.plan = updatedPlan;
        this.snackBar.open('Plan aimé !', 'Fermer', { duration: 2000 });
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Error liking plan:', err);
        this.snackBar.open('Erreur lors de l\'action "j\'aime".', 'Fermer', { duration: 3000 });
      }
    });
  }

  dislikePlan(): void {
    if (!this.plan) return;
    this.nutritionService.dislikePlan(this.plan.id).subscribe({
      next: (updatedPlan) => {
        this.plan = updatedPlan;
        this.snackBar.open('Plan non aimé !', 'Fermer', { duration: 2000 });
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Error disliking plan:', err);
        this.snackBar.open('Erreur lors de l\'action "je n\'aime pas".', 'Fermer', { duration: 3000 });
      }
    });
  }

  submitComment(): void {
    if (!this.plan || !this.newCommentText.trim()) {
      this.commentError = 'Le commentaire ne peut pas être vide';
      return;
    }

    this.isSubmittingComment = true;
    this.commentError = '';
    // Supposer que userId doit être récupéré depuis un service d'authentification ou un contexte
    const userId = 'current_user_id'; // Placeholder - remplacer par la logique réelle d'ID utilisateur

    this.nutritionService.addComment(this.plan.id, this.newCommentText, userId).subscribe({
      next: (updatedPlan) => {
        this.plan = updatedPlan; // Mettre à jour les données du plan si le backend les renvoie
        this.newCommentText = '';
        this.showCommentForm = false;
        this.isSubmittingComment = false;
        this.loadComments(this.plan!.id); // Recharger les commentaires après l'ajout
        this.snackBar.open('Commentaire ajouté avec succès !', 'Fermer', { duration: 2000 });
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Error adding comment:', err);
        this.commentError = 'Échec de l\'ajout du commentaire. Veuillez réessayer.';
        this.isSubmittingComment = false;
        this.snackBar.open('Erreur lors de l\'ajout du commentaire.', 'Fermer', { duration: 3000 });
        this.cdRef.detectChanges();
      }
    });
  }

  deleteComment(commentId: string | undefined): void {
    if (!this.plan || !commentId) return;
    if (confirm('Êtes-vous sûr de vouloir supprimer ce commentaire ?')) {
      this.nutritionService.deleteComment(this.plan.id, commentId).subscribe({
        next: (updatedPlan) => {
          this.plan = updatedPlan; // Mettre à jour les données du plan si le backend les renvoie
          this.loadComments(this.plan!.id); // Recharger les commentaires après la suppression
          this.snackBar.open('Commentaire supprimé.', 'Fermer', { duration: 2000 });
          this.cdRef.detectChanges();
        },
        error: (err) => {
          console.error('Error deleting comment:', err);
          this.snackBar.open('Erreur lors de la suppression du commentaire.', 'Fermer', { duration: 3000 });
        }
      });
    }
  }

  toggleEmailReminders(): void {
    if (!this.plan) return;
    const newState = !this.plan.emailRemindersEnabled;
    this.nutritionService.toggleEmailReminders(this.plan.id, newState).subscribe({
      next: (updatedPlan) => {
        this.plan = updatedPlan;
        this.snackBar.open(`Rappels par email ${newState ? 'activés' : 'désactivés'}.`, 'Fermer', { duration: 2000 });
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Error toggling email reminders:', err);
        this.snackBar.open('Erreur lors de la mise à jour des rappels par email.', 'Fermer', { duration: 3000 });
      }
    });
  }

  regeneratePlan(): void {
    if (!this.plan) return;
    this.loading = true; // Afficher l'indicateur de chargement
    this.nutritionService.regeneratePlan(this.plan.id).subscribe({
      next: (regeneratedPlan) => {
        this.plan = regeneratedPlan;
        this.loading = false;
        this.snackBar.open('Plan régénéré avec succès !', 'Fermer', { duration: 2000 });
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Error regenerating plan:', err);
        this.loading = false;
        this.snackBar.open('Erreur lors de la régénération du plan.', 'Fermer', { duration: 3000 });
        this.cdRef.detectChanges();
      }
    });
  }

  // --- Édition de l'horaire des repas ---
  editSchedule(): void {
    this.editingSchedule = true;
    // S'assurer que editableSchedule est une copie
    this.editableSchedule = this.plan?.mealSchedule ? { ...this.plan.mealSchedule } : this.getDefaultSchedule();
  }

  saveSchedule(): void {
    if (!this.plan) return;
    this.nutritionService.updateMealSchedule(this.plan.id, this.editableSchedule).subscribe({
      next: (updatedPlan) => {
        this.plan = updatedPlan;
        this.editingSchedule = false;
        this.snackBar.open('Horaire des repas mis à jour.', 'Fermer', { duration: 2000 });
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Error updating meal schedule:', err);
        this.snackBar.open('Erreur lors de la mise à jour de l\'horaire.', 'Fermer', { duration: 3000 });
      }
    });
  }

  cancelEditSchedule(): void {
    this.editingSchedule = false;
    // Réinitialiser editableSchedule à l'original ou par défaut si nécessaire
    this.editableSchedule = this.plan?.mealSchedule ? { ...this.plan.mealSchedule } : this.getDefaultSchedule();
  }

  // Aide à ajouter/supprimer des heures de collation (exemple)
  addSnackTime(): void {
    if (!this.editableSchedule.snackTimes) {
      this.editableSchedule.snackTimes = [];
    }
    this.editableSchedule.snackTimes.push('10:00'); // Ajouter une heure par défaut ou vide
  }

  removeSnackTime(index: number): void {
    if (this.editableSchedule.snackTimes) {
      this.editableSchedule.snackTimes.splice(index, 1);
    }
  }

  // Fonction TrackBy pour ngFor des heures de collation
  trackBySnackTime(index: number, item: string): number {
    return index;
  }

  // Méthode pour obtenir un horaire par défaut
  getDefaultSchedule(): MealSchedule {
    return {
      breakfastTime: '08:00',
      lunchTime: '13:00',
      dinnerTime: '19:00',
      snackTimes: ['10:30', '16:00']
    };
  }
}
