import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgramService } from '../ProgramService'; // Adjust path
import { MatDialog } from '@angular/material/dialog';
import { WorkoutPlayerComponent } from '../workout-player/workout-player.component'; // Adjust path
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';

// Define interfaces for better type safety
export interface Exercise {
  id: string;
  name: string;
  imageUrl?: string;
  durationMinutes: number;
  recommendedRepetitions?: string;
  difficultyLevel?: 'Easy' | 'Medium' | 'Hard' | string;
  videoUrl?: string;
  equipmentNeeded?: string;
  isActive?: boolean;
  expanded?: boolean;
}

export interface DayExercises {
  totalDuration: number;
  exercises: Exercise[];
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'NOT_STARTED';
  locked: boolean;
  dayNumber: number;
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
  isWorkoutActive: boolean = false;
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
      return;
    }
    this.loadExercises();
  }

  loadExercises(): void {
    this.programService.getDayExercises(this.assignmentId, +this.dayNumber, this.userId)
      .subscribe({
        next: (data: DayExercises) => {
          console.log('Loaded exercises for day:', this.dayNumber, data);
          this.dayExercises = {
            ...data,
            exercises: data.exercises.map(ex => ({
              ...ex,
              isActive: false,
              expanded: false
            }))
          };
          
          if (this.dayExercises.status === 'IN_PROGRESS') {
            this.isWorkoutActive = true;
          } else {
            this.isWorkoutActive = false;
          }
        },
        error: (err) => {
          console.error('Error loading exercises:', err);
          this.dayExercises = null;
        }
      });
  }

  isEmbeddableVideo(url: string): boolean {
    if (!url) return false;
    return url.includes('youtube.com/embed') || 
           url.includes('youtu.be') || 
           url.includes('player.vimeo.com/video') ||
           url.includes('vimeo.com');
  }

  getSafeVideoUrl(url: string): SafeResourceUrl {
    if (url.includes('youtu.be/')) {
      const videoId = url.split('youtu.be/')[1]?.split('?')[0];
      url = `https://www.youtube.com/embed/${videoId}`;
    } else if (url.includes('youtube.com/watch?v=')) {
      const videoId = url.split('v=')[1]?.split('&')[0];
      url = `https://www.youtube.com/embed/${videoId}`;
    }
    
    if (url.includes('vimeo.com/') && !url.includes('player.vimeo.com')) {
      const videoId = url.split('vimeo.com/')[1]?.split('?')[0];
      url = `https://player.vimeo.com/video/${videoId}`;
    }

    if (url && (url.includes('youtube.com/embed') || url.includes('player.vimeo.com/video'))) {
      return this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
    
    console.warn('Attempted to sanitize an invalid or non-embeddable video URL:', url);
    return '';
  }

  startWorkout(): void {
  if (!this.dayExercises || !this.dayExercises.exercises || this.dayExercises.exercises.length === 0) {
    console.warn("No exercises available to start.");
    return;
  }

  // Check if day is locked
  if (this.dayExercises.locked) {
    console.warn("This day is locked. Complete previous days first.");
    return;
  }

  const shouldStartDay = this.dayExercises.status === 'NOT_STARTED' || this.dayExercises.status === 'PENDING';

  if (shouldStartDay) {
    this.programService.startDay(this.assignmentId, +this.dayNumber, this.userId)
      .subscribe({
        next: () => {
          this.dayExercises!.status = 'IN_PROGRESS';
          this.isWorkoutActive = true;
          this.openWorkoutDialog();
        },
        error: (err) => {
          console.error('Error starting day via API:', err);
          this.isWorkoutActive = false;
        }
      });
  } else {
    this.isWorkoutActive = true;
    this.openWorkoutDialog();
  }
}
openWorkoutDialog(): void {
  const dialogRef = this.dialog.open(WorkoutPlayerComponent, {
    width: '100vw',
    height: '100vh',
    maxWidth: '100vw',
    panelClass: ['workout-player-dialog-fullscreen'],
    data: {
      exercises: this.dayExercises!.exercises,
      assignmentId: this.assignmentId,
      dayNumber: +this.dayNumber,
      userId: this.userId,
      totalDurationMinutes: this.dayExercises!.totalDuration,
    },
    disableClose: true
  });

  if (this.dialogSubscription) {
    this.dialogSubscription.unsubscribe();
  }

  this.dialogSubscription = dialogRef.afterClosed().subscribe(result => {
    this.isWorkoutActive = false;
    if (result?.workoutCompleted) {
      this.loadExercises(); // Refresh updated status
    } else if (result?.cancelledByUser) {
      console.log("Workout cancelled by user.");
      this.loadExercises();
    } else {
      this.loadExercises();
    }
  });
}

  editWorkout(): void {
    alert('Edit functionality to be implemented.');
    console.log('Edit workout for assignment:', this.assignmentId, 'Day:', this.dayNumber);
  }

  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement;
    if (target) {
      target.src = 'assets/placeholder-exercise.png';
    }
  }

  highlightActiveExercise(exerciseId: string): void {
    this.dayExercises?.exercises.forEach(ex => {
      ex.isActive = ex.id === exerciseId;
    });
  }

  ngOnDestroy(): void {
    if (this.dialogSubscription) {
      this.dialogSubscription.unsubscribe();
    }
  }
}