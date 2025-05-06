import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ProgramService } from '../ProgramService';


@Component({
  selector: 'app-workout-player',
  templateUrl: './workout-player.component.html',
  styleUrls: ['./workout-player.component.scss']
})
export class WorkoutPlayerComponent implements OnInit {
  currentExerciseIndex = 0;
  timeLeft: number = 0;
  timer: any;
  isCompleted = false;
  progress = 0;

  constructor(
    public dialogRef: MatDialogRef<WorkoutPlayerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      exercises: any[],
      assignmentId: string,
      dayNumber: number,
      userId: string
    },
    private programService: ProgramService
  ) {}

  ngOnInit(): void {
    this.startExercise();
  }

  startExercise(): void {
    this.timeLeft = this.currentExercise.durationMinutes * 60;
    this.startTimer();
  }

  startTimer(): void {
    this.timer = setInterval(() => {
      this.timeLeft--;
      this.progress = ((this.currentExercise.durationMinutes * 60 - this.timeLeft) / 
                     (this.currentExercise.durationMinutes * 60)) * 100;
      
      if (this.timeLeft <= 0) {
        clearInterval(this.timer);
        this.nextExercise();
      }
    }, 1000);
  }

  nextExercise(): void {
    if (this.currentExerciseIndex < this.data.exercises.length - 1) {
      this.currentExerciseIndex++;
      this.startExercise();
    } else {
      this.isCompleted = true;
      this.programService.completeDay(
        this.data.assignmentId, 
        this.data.dayNumber, 
        this.data.userId
      ).subscribe();
    }
  }

  get currentExercise(): any {
    return this.data.exercises[this.currentExerciseIndex];
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  close(): void {
    this.dialogRef.close();
  }
}