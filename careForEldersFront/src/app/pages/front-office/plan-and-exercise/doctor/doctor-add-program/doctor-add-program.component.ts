import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

import { Exercise, ExerciselistComponent } from '../exerciselist/exerciselist.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProgramService } from '../../ProgramService';

interface ProgramDay {
  dayNumber: number;
  exercises: Exercise[];
  restDay: boolean;
  warmUpMinutes?: number;
  coolDownMinutes?: number;
  instructions?: string;
  notesForPatient?: string;
}

@Component({
  selector: 'app-doctor-add-program',
  templateUrl: './doctor-add-program.component.html',
  styleUrls: ['./doctor-add-program.component.scss']
})
export class DoctorAddProgramComponent {
  programData = {
    name: '',
    description: '',
    programCategory: '',
    programImage: null as File | null
  };

  programDays: ProgramDay[] = [];
  nextDayNumber = 1;
  isLoading = false;
  doctorId = "680983836074c5474f84aaae";

  constructor(
    public dialog: MatDialog,
    private programService: ProgramService,
    private snackBar: MatSnackBar
  ) {}

  addDay(): void {
    this.programDays.push({
      dayNumber: this.nextDayNumber++,
      exercises: [],
      restDay: false
    });
  }

  openExerciseDialog(dayIndex: number): void {
    const selectedIds = this.programDays[dayIndex].exercises.map(ex => ex.id);
    
    const dialogRef = this.dialog.open(ExerciselistComponent, {
      width: '600px',
      data: { selectedExerciseIds: selectedIds }
    });
  
    dialogRef.afterClosed().subscribe((selectedExercises: Exercise[] | undefined) => {
      if (selectedExercises) {
        this.programDays[dayIndex].exercises = selectedExercises;
      }
    });
  }
  
  removeExercise(dayIndex: number, exerciseIndex: number): void {
    this.programDays[dayIndex].exercises.splice(exerciseIndex, 1);
  }

  removeDay(dayIndex: number): void {
    this.programDays.splice(dayIndex, 1);
    // Re-number remaining days
    this.programDays.forEach((day, index) => day.dayNumber = index + 1);
    this.nextDayNumber = this.programDays.length + 1;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.programData.programImage = input.files[0];
    }
  }

  toggleRestDay(day: ProgramDay): void {
    day.restDay = !day.restDay;
    if (day.restDay) {
      day.exercises = [];
    }
  }

  saveProgram(): void {
    if (!this.validateForm()) return;
  
    this.isLoading = true;
  
    const payload = {
      name: this.programData.name,
      description: this.programData.description,
      programCategory: this.programData.programCategory,
      programImage: '', // optional: you can send base64 here if needed
      days: this.programDays.map(day => ({
        dayNumber: day.dayNumber,
        exerciseIds: day.exercises.map(ex => ex.id),
        restDay: day.restDay,
        warmUpMinutes: day.warmUpMinutes || 0,
        coolDownMinutes: day.coolDownMinutes || 0,
        instructions: day.instructions || '',
        notesForPatient: day.notesForPatient || ''
      }))
    };
  
    this.programService.createProgram(payload, this.doctorId).subscribe({
      next: () => {
        this.snackBar.open('Program created successfully!', 'Close', { duration: 3000 });
        this.resetForm();
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Error creating program', 'Close', { duration: 3000 });
      },
      complete: () => this.isLoading = false
    });
  }
  

  private validateForm(): boolean {
    if (!this.programData.name || !this.programData.programCategory) {
      this.snackBar.open('Please fill in all required fields', 'Close', { duration: 3000 });
      return false;
    }
    return true;
  }

  private createFormData(): FormData {
    const formData = new FormData();
    formData.append('name', this.programData.name);
    formData.append('description', this.programData.description);
    formData.append('programCategory', this.programData.programCategory);
    if (this.programData.programImage) {
      formData.append('programImage', this.programData.programImage);
    }

    formData.append('days', JSON.stringify(this.programDays.map(day => ({
      dayNumber: day.dayNumber,
      exerciseIds: day.exercises.map(ex => ex.id),
      restDay: day.restDay,
      warmUpMinutes: day.warmUpMinutes || 0,
      coolDownMinutes: day.coolDownMinutes || 0,
      instructions: day.instructions || '',
      notesForPatient: day.notesForPatient || ''
    }))));

    return formData;
  }

  private resetForm(): void {
    this.programData = {
      name: '',
      description: '',
      programCategory: '',
      programImage: null
    };
    this.programDays = [];
    this.nextDayNumber = 1;
  }
}