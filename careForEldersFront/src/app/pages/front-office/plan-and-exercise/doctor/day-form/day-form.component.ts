import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog'; // Import MatDialog
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProgramService } from '../../ProgramService';
import { take } from 'rxjs/operators';
import { ExerciselistComponent, Exercise } from '../exerciselist/exerciselist.component'; // Adjust path as needed

// Assuming Exercise interface might be useful here too, or use 'any'
// If Exercise interface is in a shared file, import from there.
// export interface Exercise { // Duplicating for clarity if not imported from ExerciselistComponent
//   id: string;
//   name: string;
//   defaultDurationMinutes?: number; // Make sure this aligns with your data
//   categories?: string[];
//   difficultyLevel?: string;
// }


@Component({
  selector: 'app-day-form',
  templateUrl: './day-form.component.html',
  styleUrls: ['./day-form.component.scss']
})
export class DayFormComponent implements OnInit {
  dayForm: FormGroup;
  exercises: Exercise[] = []; // Use Exercise type if available
  currentDayExercises: Exercise[] = []; // For displaying and managing exercises in edit mode
  isEditMode = false;

  private originalDayNumber: number | null = null;

  constructor(
    private fb: FormBuilder,
    private programService: ProgramService,
    public dialogRef: MatDialogRef<DayFormComponent>,
    private dialog: MatDialog, // Inject MatDialog
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.dayForm = this.fb.group({
      dayNumber: [null, [Validators.required, Validators.min(1), this.validateDayNumber.bind(this)]],
      isRestDay: [false],
      warmUpMinutes: [0, [Validators.min(0)]],
      coolDownMinutes: [0, [Validators.min(0)]],
      instructions: [''],
      notesForPatient: [''],
      exerciseIds: [[]]
    });
  }

    ngOnInit(): void {
    console.log('DayFormComponent initialized with data (raw):', this.data);

    this.programService.getAllExercises().pipe(take(1)).subscribe(allExercises => {
      this.exercises = allExercises; // This is the master list of all available exercises

      if (this.data.day) { // EDIT MODE
        this.isEditMode = true;
        this.originalDayNumber = this.data.day.dayNumber;

        // --- START OF CORRECTION ---
        // Extract exercise IDs from the nested 'exercises' array within data.day
        // The log shows: this.data.day.exercises = Array(1) [ {id: '...', name: '...'} ]
        const dayExerciseIdsFromData = (this.data.day.exercises || [])
                                          .map((ex: any) => ex.id)
                                          .filter((id: string | null) => id != null); // Ensure IDs are valid

        console.log('Extracted exercise IDs for edit mode:', dayExerciseIdsFromData);
        // --- END OF CORRECTION ---

        this.dayForm.patchValue({
          dayNumber: this.data.day.dayNumber,
          isRestDay: !!this.data.day.restDay,
          warmUpMinutes: this.data.day.warmUpMinutes ?? 0,
          coolDownMinutes: this.data.day.coolDownMinutes ?? 0,
          instructions: this.data.day.instructions || '',
          notesForPatient: this.data.day.notesForPatient || '',
          exerciseIds: dayExerciseIdsFromData // Patch the form control with the extracted IDs
        });

        // Populate currentDayExercises for edit mode display using the master list (this.exercises)
        // and the IDs we just extracted.
        if (dayExerciseIdsFromData.length > 0 && this.exercises.length > 0) {
          this.currentDayExercises = this.exercises.filter(masterEx =>
            dayExerciseIdsFromData.includes(masterEx.id)
          );
        } else {
          this.currentDayExercises = [];
        }
        // Log the populated list for verification
        console.log('Populated currentDayExercises for edit mode:', this.currentDayExercises);
        console.log('Form patched with (edit mode):', this.dayForm.value);

      } else { // ADD NEW DAY MODE
        this.isEditMode = false;
        const existingDayNumbers = this.data.existingDayNumbers || [];
        const nextDayNumber = existingDayNumbers.length > 0 ? Math.max(...existingDayNumbers.map(Number)) + 1 : 1;
        this.dayForm.get('dayNumber')?.setValue(nextDayNumber);
        this.dayForm.get('dayNumber')?.disable();
        console.log('Form set for new day:', this.dayForm.value);
      }
    });

    // Subscribe to isRestDay changes
    this.dayForm.get('isRestDay')?.valueChanges.subscribe(isRest => {
      if (isRest) {
        this.currentDayExercises = [];
        this.dayForm.get('exerciseIds')?.setValue([]);
        this.dayForm.get('warmUpMinutes')?.setValue(0);
        this.dayForm.get('warmUpMinutes')?.disable();
        this.dayForm.get('coolDownMinutes')?.setValue(0);
        this.dayForm.get('coolDownMinutes')?.disable();
        this.dayForm.get('exerciseIds')?.disable(); // Also disable the control itself
      } else {
        this.dayForm.get('warmUpMinutes')?.enable();
        this.dayForm.get('coolDownMinutes')?.enable();
        this.dayForm.get('exerciseIds')?.enable(); // Re-enable the control
      }
    });
  }

  validateDayNumber(control: AbstractControl): { [key: string]: any } | null {
    if (!this.isEditMode || !this.data.existingDayNumbers) {
      return null;
    }
    const newDayNumber = control.value;
    if (newDayNumber === null || newDayNumber === undefined) {
      return null;
    }
    const isDuplicate = this.data.existingDayNumbers
      .filter((n: number) => n !== this.originalDayNumber)
      .includes(Number(newDayNumber));
    return isDuplicate ? { 'dayNumberExists': true } : null;
  }

  removeExercise(exerciseIdToRemove: string): void {
    this.currentDayExercises = this.currentDayExercises.filter(ex => ex.id !== exerciseIdToRemove);
    const updatedExerciseIds = this.currentDayExercises.map(ex => ex.id);
    this.dayForm.get('exerciseIds')?.setValue(updatedExerciseIds);
    console.log('Removed exercise, updated currentDayExercises:', this.currentDayExercises);
  }

  openAddExerciseDialog(): void {
    const currentExerciseIds = this.currentDayExercises.map(ex => ex.id);
    const dialogRef = this.dialog.open(ExerciselistComponent, {
      width: 'clamp(600px, 80vw, 900px)',
      maxWidth: '95vw',
      data: {
        selectedExerciseIds: currentExerciseIds
      },
    });

    dialogRef.afterClosed().subscribe((selectedExercisesToAdd: Exercise[] | undefined) => {
      if (selectedExercisesToAdd && selectedExercisesToAdd.length > 0) {
        selectedExercisesToAdd.forEach(newEx => {
          if (!this.currentDayExercises.find(existingEx => existingEx.id === newEx.id)) {
            // Ensure the exercise from dialog is compatible with Exercise type here (e.g. has defaultDurationMinutes)
            // You might need to fetch full details if ExerciselistComponent only returns partial data
            const fullExerciseDetails = this.exercises.find(e => e.id === newEx.id);
            if (fullExerciseDetails) {
              this.currentDayExercises.push(fullExerciseDetails);
            }
          }
        });
        const updatedExerciseIds = this.currentDayExercises.map(ex => ex.id);
        this.dayForm.get('exerciseIds')?.setValue(updatedExerciseIds);
        console.log('Added exercises, updated currentDayExercises:', this.currentDayExercises);
      }
    });
  }

  saveDay() {
    if (this.dayForm.invalid) {
      this.dayForm.markAllAsTouched();
      console.error('Form is invalid:', this.dayForm.errors);
      Object.keys(this.dayForm.controls).forEach(key => {
        const controlErrors = this.dayForm.get(key)?.errors;
        if (controlErrors != null) {
          console.error('Key = ' + key + ', error = ' + JSON.stringify(controlErrors));
        }
      });
      return;
    }

    const formValue = this.dayForm.getRawValue(); // Use getRawValue for disabled fields like dayNumber
    const isRestDay = formValue.isRestDay;

    let exerciseIdsForSave: string[];
    if (isRestDay) {
      exerciseIdsForSave = [];
    } else {
      if (this.isEditMode) {
        exerciseIdsForSave = this.currentDayExercises.map(ex => ex.id);
      } else {
        exerciseIdsForSave = formValue.exerciseIds || [];
      }
    }

    const dayData = {
      dayNumber: Number(formValue.dayNumber),
      restDay: isRestDay,
      warmUpMinutes: isRestDay ? 0 : Number(formValue.warmUpMinutes),
      coolDownMinutes: isRestDay ? 0 : Number(formValue.coolDownMinutes),
      instructions: formValue.instructions,
      notesForPatient: formValue.notesForPatient,
      exerciseIds: exerciseIdsForSave
    };

    if (this.isEditMode) {
      if (!this.data.day || !this.data.day.id) {
        console.error('Cannot update day: Missing day ID.', this.data.day);
        return;
      }
      console.log('Updating day with ID:', this.data.day.id, 'Data:', dayData);
      this.programService.updateProgramDay(
        this.data.programId,
        this.data.day.id,
        dayData,
        this.data.doctorId
      ).pipe(take(1)).subscribe({
        next: updatedDay => this.dialogRef.close({ day: updatedDay, action: 'updated' }),
        error: err => console.error('Failed to update day:', err)
      });
    } else {
      console.log('Adding new day. Data:', dayData);
      this.programService.addDayToProgram(
        this.data.programId,
        dayData,
        this.data.doctorId
      ).pipe(take(1)).subscribe({
        next: addedDay => this.dialogRef.close({ day: addedDay, action: 'added' }),
        error: err => console.error('Failed to add day:', err)
      });
    }
  }
}