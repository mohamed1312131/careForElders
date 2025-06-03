import { Component, Inject, OnInit, SecurityContext } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

// Angular Material & Common Modules for Standalone Component
import { CommonModule, DatePipe } from '@angular/common';
import { ProgramService } from '../ProgramService';
import { ProgramInfoComponent } from '../doctor/program-info/program-info.component';


// Define more specific types for program and patient for better clarity
export interface ExerciseInfo {
  id: string;
  name: string;
  description?: string;
  imageUrl?: string;
  defaultDurationMinutes?: number;
  difficultyLevel?: string;
  equipmentNeeded?: string[]; // This is optional
  videoTutorialUrl?: string;
  safeVideoUrl?: SafeResourceUrl; // For embedded video
}

export interface DayInfo {
  id: string;
  dayNumber: number;
  restDay: boolean;
  warmUpMinutes?: number;
  coolDownMinutes?: number;
  instructions?: string;
  notesForPatient?: string;
  exercises?: ExerciseInfo[];
}

export interface ProgramDetails {
  id: string;
  name: string;
  description?: string;
  programImage?: string;
  programCategory?: string;
  createdBy?: string;
  createdDate?: string | Date;
  updatedDate?: string | Date;
  days?: DayInfo[];
}

export interface PatientInfo {
  id: string;
  name: string;
  email?: string;
  assignedDate?: string | Date;
}
@Component({
  selector: 'app-user-program-info',
  templateUrl: './user-program-info.component.html',
  styleUrl: './user-program-info.component.scss'
})
export class UserProgramInfoComponent implements OnInit {
  isLoading = true;
  program: ProgramDetails | null = null;
  patients: PatientInfo[] = [];
  errorLoading = false;
  errorMessage: string = '';

  constructor(
    public dialogRef: MatDialogRef<ProgramInfoComponent>,
    private programService: ProgramService,
    private snackBar: MatSnackBar,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public data: { programId: string }
  ) {}

  ngOnInit(): void {
    if (!this.data.programId) {
      this.handleError('No Program ID provided.');
      return;
    }
    this.loadProgramDetails();
  }

  // Make this method public so it can be called from the template
  public loadProgramDetails(): void {
    this.isLoading = true;
    this.errorLoading = false;
    this.programService.getProgramDetails(this.data.programId).subscribe({
      next: (programData: ProgramDetails) => {
        this.program = {
          ...programData,
          days: programData.days?.map(day => ({
            ...day,
            exercises: day.exercises?.map(ex => ({
              ...ex,
              safeVideoUrl: ex.videoTutorialUrl ? this.getSafeVideoUrl(ex.videoTutorialUrl) : undefined
            }))
          }))
        };
        this.loadPatients();
      },
      error: (err) => {
        console.error("Error loading program details:", err);
        this.handleError('Failed to load program details. Please try again.');
      }
    });
  }

  private loadPatients(): void {
    this.programService.getProgramPatients(this.data.programId).subscribe({
      next: (patientsData: PatientInfo[]) => {
        this.patients = patientsData;
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Error loading patients:", err);
        this.snackBar.open('Could not load assigned patients.', 'Close', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  private handleError(message: string): void {
    this.errorMessage = message;
    this.errorLoading = true;
    this.isLoading = false;
    this.snackBar.open(message, 'Close', { duration: 5000, panelClass: ['error-snackbar'] });
  }

  getSafeVideoUrl(url: string): SafeResourceUrl | undefined {
    if (!url) return undefined;
     // Basic check for YouTube URL to attempt to transform to embeddable URL
    // Example: https://www.youtube.com/watch?v=VIDEO_ID -> https://www.youtube.com/embed/VIDEO_ID
    if (url.includes('youtube.com/watch?v=')) {
      const videoId = url.split('v=')[1].split('&')[0]; // Extract video ID
      return this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${videoId}`);
    } else if (url.includes('youtu.be/')) {
      const videoId = url.split('youtu.be/')[1].split('?')[0]; // Extract video ID from short URL
      return this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${videoId}`);
    }
    // For other URLs, sanitize as is, assuming they are direct embed links or other platforms
    // you might want to add more specific parsers here if needed.
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }


  closeDialog(): void {
    this.dialogRef.close();
  }
}