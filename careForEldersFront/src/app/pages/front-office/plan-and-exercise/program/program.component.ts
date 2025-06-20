import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgramService } from '../ProgramService'; // Adjust path
import { MatDialog } from '@angular/material/dialog';
import { WorkoutPlayerComponent } from '../workout-player/workout-player.component'; // Adjust path
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';

// Define interfaces for better type safety
export interface Exercise {
  id: string; // or number
  name: string;
  imageUrl?: string; // Make optional if not always present
  durationMinutes: number;
  recommendedRepetitions?: string;
  difficultyLevel?: 'Easy' | 'Medium' | 'Hard' | string; // Allow string for flexibility
  videoUrl?: string;
  equipmentNeeded?: string; // Example of another detail
  // Internal state for UI
  isActive?: boolean;
  expanded?: boolean;
}

export interface DayExercises {
  totalDuration: number;
  exercises: Exercise[];
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED'; // Use specific statuses
}

@Component({
  selector: 'app-program',
  templateUrl: './program.component.html',
  styleUrls: ['./program.component.scss']
})
export class ProgramComponent implements OnInit, OnDestroy {
  dayExercises: DayExercises | null = null;
  assignmentId!: string;
  dayNumber!: string;
  userId!: string;

  isWorkoutActive: boolean = false; // To show a general "workout in progress" indicator
  private dialogSubscription!: Subscription;

  constructor(
    private route: ActivatedRoute,
    private programService: ProgramService,
    private dialog: MatDialog,
    private sanitizer: DomSanitizer,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dayNumber = this.route.snapshot.paramMap.get('dayNumber')!;
    this.assignmentId = this.route.snapshot.paramMap.get('assignmentId')!;
    this.userId = localStorage.getItem('user_id') || '';

    if (!this.userId) {
      console.error("User ID not found. Redirecting or showing error.");
      // Potentially navigate to a login page or display an error message
      // this.router.navigate(['/login']);
      return;
    }
    this.loadExercises();
  }

  loadExercises(): void {
    this.programService.getDayExercises(this.assignmentId, +this.dayNumber, this.userId)
      .subscribe({
        next: (data: DayExercises) => {
          this.dayExercises = {
            ...data,
            exercises: data.exercises.map(ex => ({
              ...ex,
              isActive: false, // Initialize UI state properties
              expanded: false
            }))
          };
          // If coming back to this page and a workout was active, update UI indicator.
          // This is a simple check; more complex state (like which exercise was active)
          // would need to be persisted and restored if the player isn't modal.
          if (this.dayExercises.status === 'IN_PROGRESS') {
              this.isWorkoutActive = true; // Show general indicator
          } else {
              this.isWorkoutActive = false;
          }
        },
        error: (err) => {
          console.error('Error loading exercises:', err);
          this.dayExercises = null; // Important to handle template *ngIf
        }
      });
  }

  getSafeVideoUrl(url: string): SafeResourceUrl {
    // Ensure URL is a valid embeddable URL (basic check)
    if (url && (url.includes('youtube.com/embed') || url.includes('player.vimeo.com/video'))) {
      return this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
    console.warn('Attempted to sanitize an invalid or non-embeddable video URL:', url);
    return ''; // Return empty or a placeholder, but an empty src for iframe is usually fine
  }

  completeDay(): void {
    this.programService.completeDay(this.assignmentId, +this.dayNumber, this.userId)
      .subscribe({
        next: () => {
          this.isWorkoutActive = false;
          this.loadExercises(); // Refresh data to show 'COMPLETED' status
        },
        error: (err) => console.error('Error completing day:', err)
      });
  }

  startWorkout(): void {
    if (!this.dayExercises || !this.dayExercises.exercises || this.dayExercises.exercises.length === 0) {
      console.warn("No exercises available to start.");
      // Optionally show a user-friendly message (e.g., using MatSnackBar)
      return;
    }

    const startApiCall = this.dayExercises.status !== 'IN_PROGRESS' ?
                         this.programService.startDay(this.assignmentId, +this.dayNumber, this.userId) :
                         Promise.resolve(); // If already in progress, no need to call API again

    Promise.resolve(startApiCall).then(() => { // Handles both Observable and Promise resolve
      if (this.dayExercises) this.dayExercises.status = 'IN_PROGRESS'; // Optimistically update
      this.isWorkoutActive = true;

      const dialogRef = this.dialog.open(WorkoutPlayerComponent, {
        width: '100vw', // Full width
        height: '100vh', // Full height
        maxWidth: '100vw', // Ensure it takes full viewport on mobile
        panelClass: ['workout-player-dialog-fullscreen'], // Custom class for fullscreen styling
        data: {
          exercises: this.dayExercises!.exercises, // Non-null assertion as we checked above
          assignmentId: this.assignmentId,
          dayNumber: this.dayNumber,
          userId: this.userId,
          totalDurationMinutes: this.dayExercises!.totalDuration,
          // You can pass current progress if resuming a persisted state
        },
        disableClose: true // Important: prevent closing via escape or backdrop click
      });

      if (this.dialogSubscription) {
        this.dialogSubscription.unsubscribe();
      }

      this.dialogSubscription = dialogRef.afterClosed().subscribe(result => {
        this.isWorkoutActive = false; // Workout player is closed
        // Result can contain information like { workoutCompleted: true } or { progress: ... }
        if (result?.workoutCompleted) {
          // The WorkoutPlayerComponent might call the completeDay service itself,
          // or expect this component to do it. For simplicity, let's assume this component reloads.
          this.loadExercises(); // Refresh to get the 'COMPLETED' status
        } else if (result?.cancelledByUser && this.dayExercises) {
            // If user cancels, keep status as IN_PROGRESS unless specified otherwise
            // No specific API call here unless you want to track cancellations
            console.log("Workout cancelled by user. Current status:", this.dayExercises.status);
            this.loadExercises(); // Refresh to ensure UI consistency
        } else {
          // Any other close reason (e.g., error in player, or simply closed without finishing)
          // Reload to get the latest server state.
          this.loadExercises();
        }
      });
    }).catch(err => {
        console.error('Error starting day via API:', err);
        this.isWorkoutActive = false; // Revert indicator if API call failed
    });
  }

  // Optional: If you want to allow editing the workout plan
  editWorkout(): void {
    // Example: navigate to an edit route
    // this.router.navigate(['/edit-program', this.assignmentId, 'day', this.dayNumber]);
    alert('Edit functionality to be implemented.');
    console.log('Edit workout for assignment:', this.assignmentId, 'Day:', this.dayNumber);
  }

  // Method to highlight exercise from WorkoutPlayer (if needed and player isn't fully modal)
  highlightActiveExercise(exerciseId: string): void {
    this.dayExercises?.exercises.forEach(ex => {
      ex.isActive = ex.id === exerciseId;
      if (ex.isActive) {
        // Potentially scroll this item into view if list is long
        // document.getElementById(`exercise-${ex.id}`)?.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.dialogSubscription) {
      this.dialogSubscription.unsubscribe();
    }
  }
}