import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatListModule } from '@angular/material/list'; // Keep if you use mat-list specific features elsewhere, not strictly needed for this template
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { ProgramService } from '../../ProgramService'; // Ensure this path is correct
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Import spinner

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
    // MatListModule, // Only if specifically needed for mat-list directives
    MatCheckboxModule,
    MatButtonModule,
    MatProgressSpinnerModule // Add spinner module
  ]
})
export class ExerciselistComponent implements OnInit {
  availableExercises: SelectableExercise[] = [];
  filteredExercises: SelectableExercise[] = [];

  isLoading = true;
  errorMessage: string | null = null;

  selectedCategory: string = '';
  selectedDifficulty: string = '';

  // Consider fetching these from a service or config if they change often
  categories: string[] = ['Cardio', 'Strength', 'Flexibility', 'Balance', 'Mobility', 'Core', 'Upper Body', 'Lower Body'];
  difficultyLevels: string[] = ['Beginner', 'Intermediate', 'Advanced', 'All Levels'];

  constructor(
    public dialogRef: MatDialogRef<ExerciselistComponent>,
    private exerciseService: ProgramService, // Renamed from programService to exerciseService for clarity based on usage
    @Inject(MAT_DIALOG_DATA) public data: { selectedExerciseIds: string[] }
  ) {}

  ngOnInit(): void {
    this.loadExercises();
  }

  private loadExercises(): void {
    this.isLoading = true; // Ensure loading is true at the start
    this.errorMessage = null; // Reset error message
    this.exerciseService.getAllExercises() // Assuming ProgramService is your exercise service
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (exercises) => {
          this.availableExercises = exercises.map(ex => ({
            ...ex,
            selected: this.data?.selectedExerciseIds?.includes(ex.id) || false
          }));
          this.filterExercises(); // Apply initial filter (which will set filteredExercises)
        },
        error: (err) => {
          console.error('Error loading exercises:', err);
          this.errorMessage = 'Failed to load exercises. Please try again later.';
          this.filteredExercises = []; // Clear exercises on error
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
      .map(({ selected, ...rest }) => rest); // Remove the 'selected' property

    this.dialogRef.close(selectedExercises);
  }

  // Helper to check if any exercise is selected
  anySelected(): boolean {
    return this.availableExercises.some(ex => ex.selected);
  }

  // Allow clicking the row to toggle selection
  toggleSelection(exercise: SelectableExercise, event: MouseEvent): void {
    // Prevent click from checkbox itself triggering this again
    if ((event.target as HTMLElement).closest('mat-checkbox')) {
        return;
    }
    exercise.selected = !exercise.selected;
  }
}