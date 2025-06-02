import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ProgramService } from '../ProgramService';

@Component({
  selector: 'app-workout-player',
  templateUrl: './workout-player.component.html',
  styleUrls: ['./workout-player.component.scss']
})
export class WorkoutPlayerComponent implements OnInit, OnDestroy {
  currentExerciseIndex = 0;
  timeLeft: number = 0;
  timer: any;
  isCompleted = false;
  progress = 0;
  isPaused = false;
  workoutStartTime = new Date();

  constructor(
    public dialogRef: MatDialogRef<WorkoutPlayerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      exercises: any[],
      assignmentId: string,
      dayNumber: number,
      userId: string,
      totalDurationMinutes?: number
    },
    private programService: ProgramService
  ) {}

  ngOnInit(): void {
    if (this.data.exercises && this.data.exercises.length > 0) {
      this.startExercise();
    }
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  startExercise(): void {
    if (this.currentExercise) {
      this.timeLeft = this.currentExercise.durationMinutes * 60;
      this.progress = 0;
      this.startTimer();
    }
  }

  startTimer(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
    
    this.timer = setInterval(() => {
      if (!this.isPaused) {
        this.timeLeft--;
        this.progress = ((this.currentExercise.durationMinutes * 60 - this.timeLeft) / 
                       (this.currentExercise.durationMinutes * 60)) * 100;
        
        if (this.timeLeft <= 0) {
          clearInterval(this.timer);
          this.nextExercise();
        }
      }
    }, 1000);
  }

  pauseTimer(): void {
    this.isPaused = !this.isPaused;
  }

  skipExercise(): void {
    clearInterval(this.timer);
    this.nextExercise();
  }

  nextExercise(): void {
    if (this.currentExerciseIndex < this.data.exercises.length - 1) {
      this.currentExerciseIndex++;
      this.startExercise();
    } else {
      this.completeWorkout();
    }
  }

  completeWorkout(): void {
    this.isCompleted = true;
    clearInterval(this.timer);
    
    // Calculate actual duration
    const workoutEndTime = new Date();
    const actualDurationMinutes = Math.ceil((workoutEndTime.getTime() - this.workoutStartTime.getTime()) / (1000 * 60));
    
    // Complete the day via API
    this.programService.completeDay(
      this.data.assignmentId, 
      this.data.dayNumber, 
      this.data.userId
    ).subscribe({
      next: (response) => {
        console.log('Day completed successfully:', response);
        // Close dialog with success result
        this.dialogRef.close({ 
          workoutCompleted: true, 
          actualDurationMinutes: actualDurationMinutes 
        });
      },
      error: (error) => {
        console.error('Error completing day:', error);
        // Still close but indicate there was an error
        this.dialogRef.close({ 
          workoutCompleted: false, 
          error: error 
        });
      }
    });
  }

  get currentExercise(): any {
    return this.data.exercises[this.currentExerciseIndex];
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  getProgressPercentage(): number {
    return Math.round(((this.currentExerciseIndex + (this.progress / 100)) / this.data.exercises.length) * 100);
  }

  close(): void {
    // Ask for confirmation if workout is in progress
    if (!this.isCompleted && this.currentExerciseIndex > 0) {
      const confirmClose = confirm('Are you sure you want to quit the workout? Your progress will not be saved.');
      if (!confirmClose) {
        return;
      }
    }
    
    clearInterval(this.timer);
    this.dialogRef.close({ 
      cancelledByUser: true, 
      currentExerciseIndex: this.currentExerciseIndex 
    });
  }
}