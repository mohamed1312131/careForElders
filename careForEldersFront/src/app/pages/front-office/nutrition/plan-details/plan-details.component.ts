import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { NutritionService } from '../../../../services/nutrition.service';
import { NutritionPlan } from '../../../../models/nutrition-plan.model';

@Component({
  selector: 'app-plan-details',
  templateUrl: './plan-details.component.html',
  styleUrls: ['./plan-details.component.scss']
})
export class PlanDetailsComponent implements OnInit {
  plan: NutritionPlan | null = null;
  loading = true;
  showCommentForm = false;
  newComment = '';
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private nutritionService: NutritionService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.nutritionService.getPlanById(id).subscribe({
        next: (plan) => {
          this.plan = plan;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching plan:', err);
          this.loading = false;
        }
      });
    }
  }
  likePlan(): void {
    if (!this.plan) return;

    this.nutritionService.likePlan(this.plan.id).subscribe({
      next: (updatedPlan) => {
        this.plan = updatedPlan;
      },
      error: (err) => console.error('Error liking plan:', err)
    });
  }

  dislikePlan(): void {
    if (!this.plan) return;

    this.nutritionService.dislikePlan(this.plan.id).subscribe({
      next: (updatedPlan) => {
        this.plan = updatedPlan;
      },
      error: (err) => console.error('Error disliking plan:', err)
    });
  }

  addComment(): void {
    if (!this.plan || !this.newComment.trim()) return;

    this.nutritionService.addComment(this.plan.id, this.newComment).subscribe({
      next: (updatedPlan) => {
        this.plan = updatedPlan;
        this.newComment = '';
        this.showCommentForm = false;
      },
      error: (err) => console.error('Error adding comment:', err)
    });
  }
  isSubmitting = false;
  error = '';

  submitComment(): void {
    if (!this.newComment.trim()) {
      this.error = 'Comment cannot be empty';
      return;
    }

    this.isSubmitting = true;
    this.error = '';
    //this.commentAdded.emit(this.newComment);
    this.newComment = '';
    this.isSubmitting = false;
  }
}
