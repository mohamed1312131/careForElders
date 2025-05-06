import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProgramService } from '../../ProgramService';

@Component({
  selector: 'app-add-exercise',
  templateUrl: './add-exercise.component.html',
  styleUrls: ['./add-exercise.component.scss']
})
export class AddExerciseComponent {
  exerciseForm: FormGroup;
  categories: string[] = ['Cardio', 'Strength', 'Flexibility', 'Balance', 'Mobility'];
  difficultyLevels: string[] = ['Beginner', 'Intermediate', 'Advanced'];
  doctorId = "680983836074c5474f84aaae"; // Get from auth service

  formSubmitted = false;
  imageFile: File | null = null;
  videoFile: File | null = null;
  imageError = false;
  videoError = false;

  constructor(
    private fb: FormBuilder,
    private exerciseService: ProgramService,
    private snackBar: MatSnackBar
  ) {
    this.exerciseForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      defaultDurationMinutes: ['', [Validators.required, Validators.min(1)]],
      categories: [[], Validators.required],
      difficultyLevel: ['', Validators.required]
    });
  }

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.imageFile = file;
      this.imageError = false;
    }
  }

  onVideoSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.videoFile = file;
      this.videoError = false;
    }
  }

  onSubmit() {
    this.formSubmitted = true;

    if (!this.imageFile) this.imageError = true;
    if (!this.videoFile) this.videoError = true;

    if (this.exerciseForm.invalid || this.imageError || this.videoError) {
      return;
    }

    const formData = new FormData();
    formData.append('name', this.exerciseForm.get('name')?.value);
    formData.append('description', this.exerciseForm.get('description')?.value);
    formData.append('defaultDurationMinutes', this.exerciseForm.get('defaultDurationMinutes')?.value);
    formData.append('categories', JSON.stringify(this.exerciseForm.get('categories')?.value)); // It's an array
    formData.append('difficultyLevel', this.exerciseForm.get('difficultyLevel')?.value);
    formData.append('image', this.imageFile as File);
    formData.append('video', this.videoFile as File);
    formData.append('doctorId', this.doctorId);

    this.exerciseService.createExercise(this.exerciseForm.value, this.doctorId)
    .subscribe({
      next: () => {
        this.snackBar.open('Exercise created successfully!', 'Close', { duration: 3000 });
        this.exerciseForm.reset();
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Error creating exercise', 'Close', { duration: 3000 });
      }
    });
  }
}
