import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatListModule } from '@angular/material/list';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { ProgramService } from '../../ProgramService';

export interface Exercise {
  id: string;
  name: string;
  categories: string[];
  difficultyLevel: string;
  // any other fields you want
}

interface SelectableExercise extends Exercise {
  selected: boolean;
}

@Component({
  selector: 'app-exerciselist',
  templateUrl: './exerciselist.component.html',
  styleUrls: ['./exerciselist.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatListModule,
    MatCheckboxModule,
    MatButtonModule
  ]
})
export class ExerciselistComponent implements OnInit {
  availableExercises: SelectableExercise[] = [];
  filteredExercises: SelectableExercise[] = [];

  isLoading = true;
  errorMessage: string | null = null;

  selectedCategory: string = '';
  selectedDifficulty: string = '';

  categories: string[] = ['Cardio', 'Strength', 'Flexibility', 'Balance', 'Mobility'];
  difficultyLevels: string[] = ['Beginner', 'Intermediate', 'Advanced'];

  constructor(
    public dialogRef: MatDialogRef<ExerciselistComponent>,
    private exerciseService: ProgramService,
    @Inject(MAT_DIALOG_DATA) public data: { selectedExerciseIds: string[] }
  ) {}

  ngOnInit(): void {
    this.loadExercises();
  }

  private loadExercises(): void {
    this.exerciseService.getAllExercises()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (exercises) => {
          console.log('Exercises loaded:', exercises);
          this.availableExercises = exercises.map(ex => ({
            ...ex,
            selected: this.data?.selectedExerciseIds?.includes(ex.id) || false
          }));
          this.filteredExercises = [...this.availableExercises];
        },
        error: (err) => {
          console.error('Error loading exercises:', err);
          this.errorMessage = 'Failed to load exercises. Please try again later.';
        }
      });
  }

  filterExercises() {
    this.filteredExercises = this.availableExercises.filter(ex => {
      const matchesCategory = this.selectedCategory ? ex.categories.includes(this.selectedCategory) : true;
      const matchesDifficulty = this.selectedDifficulty ? ex.difficultyLevel === this.selectedDifficulty : true;
      return matchesCategory && matchesDifficulty;
    });
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

  onAddClick(): void {
    const selectedExercises = this.availableExercises
      .filter(ex => ex.selected)
      .map(({ selected, ...rest }) => rest);

    this.dialogRef.close(selectedExercises);
  }
}