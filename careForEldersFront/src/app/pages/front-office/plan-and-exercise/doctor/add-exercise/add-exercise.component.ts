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
  exerciseForm!: FormGroup;
  selectedImageFile: File | null = null;
  selectedVideoFile: File | null = null;
  isLoading = false;
  doctorId! :string;

  imageUploadError: string | null = null;
  videoUploadError: string | null = null;
  readonly MAX_FILE_SIZE_MB = 10; // Max file size in MB for frontend validation
  readonly MAX_FILE_SIZE_BYTES = this.MAX_FILE_SIZE_MB * 1024 * 1024;


  constructor(
    private fb: FormBuilder,
    private programService: ProgramService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.doctorId = localStorage.getItem('user-id') || '';
    this.exerciseForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      defaultDurationMinutes: [null, [Validators.required, Validators.min(1)]],
      categories: [''], // Comma-separated string
      difficultyLevel: ['Beginner'],
      equipmentNeeded: [''], // Comma-separated string
      targetMuscleGroup: ['']
      // imageUrl and videoTutorialUrl will be set by the backend
    });
  }

  onImageFileSelected(event: Event): void {
    this.imageUploadError = null;
    const element = event.currentTarget as HTMLInputElement;
    const fileList: FileList | null = element.files;
    if (fileList && fileList.length > 0) {
      const file = fileList[0];
      if (!file.type.startsWith('image/')) {
        this.imageUploadError = 'Invalid file type. Please select an image.';
        this.selectedImageFile = null;
        element.value = ''; // Clear the input
        return;
      }
      if (file.size > this.MAX_FILE_SIZE_BYTES) {
        this.imageUploadError = `File is too large. Maximum size is ${this.MAX_FILE_SIZE_MB}MB.`;
        this.selectedImageFile = null;
        element.value = ''; // Clear the input
        return;
      }
      this.selectedImageFile = file;
    } else {
      this.selectedImageFile = null;
    }
  }
  clearImageFile(): void {
    this.selectedImageFile = null;
    this.imageUploadError = null;
    // Also reset the input field if you have a reference to it
    const imageInput = document.getElementById('imageFile') as HTMLInputElement;
    if (imageInput) {
        imageInput.value = '';
    }
  }

  onVideoFileSelected(event: Event): void {
    this.videoUploadError = null;
    const element = event.currentTarget as HTMLInputElement;
    const fileList: FileList | null = element.files;
    if (fileList && fileList.length > 0) {
      const file = fileList[0];
      if (!file.type.startsWith('video/')) {
        this.videoUploadError = 'Invalid file type. Please select a video.';
        this.selectedVideoFile = null;
        element.value = ''; // Clear the input
        return;
      }
      // You might want a different (larger) size limit for videos
      if (file.size > this.MAX_FILE_SIZE_BYTES * 5) { // Example: 50MB for videos
        this.videoUploadError = `File is too large. Maximum size for videos is ${this.MAX_FILE_SIZE_MB * 5}MB.`;
        this.selectedVideoFile = null;
        element.value = ''; // Clear the input
        return;
      }
      this.selectedVideoFile = file;
    } else {
      this.selectedVideoFile = null;
    }
  }

  clearVideoFile(): void {
    this.selectedVideoFile = null;
    this.videoUploadError = null;
    const videoInput = document.getElementById('videoFile') as HTMLInputElement;
    if (videoInput) {
        videoInput.value = '';
    }
  }


  onSubmit(): void {
    if (this.exerciseForm.invalid || this.imageUploadError || this.videoUploadError) {
      this.snackBar.open('Please correct the errors in the form.', 'Close', { duration: 3000, panelClass: ['error-snackbar'] });
      return;
    }

    this.isLoading = true;

    const formValue = this.exerciseForm.value;
    const exerciseData = {
      name: formValue.name,
      description: formValue.description,
      defaultDurationMinutes: formValue.defaultDurationMinutes,
      categories: formValue.categories ? formValue.categories.split(',').map((s: string) => s.trim()).filter((s: string) => s) : [],
      difficultyLevel: formValue.difficultyLevel,
      equipmentNeeded: formValue.equipmentNeeded ? formValue.equipmentNeeded.split(',').map((s: string) => s.trim()).filter((s: string) => s) : [],
      targetMuscleGroup: formValue.targetMuscleGroup
    };
    console.log("execise data",exerciseData);
    this.programService.createExercise(
      exerciseData,
      this.selectedImageFile,
      this.selectedVideoFile,
      this.doctorId
    ).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.snackBar.open('Exercise created successfully!', 'Close', { duration: 3000, panelClass: ['success-snackbar'] });
        this.exerciseForm.reset({ difficultyLevel: 'Beginner' }); // Reset form to initial state
        this.clearImageFile();
        this.clearVideoFile();
        // Optionally navigate or refresh list
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error creating exercise:', error);
        const errorMessage = error.error?.message || error.message || 'Failed to create exercise. Please try again.';
        this.snackBar.open(errorMessage, 'Close', { duration: 5000, panelClass: ['error-snackbar'] });
      }
    });
  }
}