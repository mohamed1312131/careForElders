// program.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProgramService } from '../ProgramService';
import { MatDialog } from '@angular/material/dialog';
import { WorkoutPlayerComponent } from '../workout-player/workout-player.component';


@Component({
  selector: 'app-program',
  templateUrl: './program.component.html',
  styleUrls: ['./program.component.scss']
})
export class ProgramComponent implements OnInit {
  dayExercises!: any;
  assignmentId!: string;
  dayNumber!: number;

  constructor(
    private route: ActivatedRoute,
    private programService: ProgramService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
   // this.assignmentId = this.route.snapshot.paramMap.get('assignmentId')!;
    //this.dayNumber = +this.route.snapshot.paramMap.get('dayNumber')!;
    this.dayNumber = 1; // For testing purposes, replace with actual route param
    this.assignmentId = '680cfda7bd6be81407cdbabe'; // For testing purposes, replace with actual route param
    this.loadExercises();
  }

  loadExercises(): void {
    const userId = '680a2319bd2b9864caf53529'; // Get from auth service
    this.programService.getDayExercises(this.assignmentId, this.dayNumber, userId)
      .subscribe({
        next: (data) => this.dayExercises = data,
        error: (err) => console.error(err)
      });
  }



  completeDay(): void {
    const userId = '680a2319bd2b9864caf53529'; // Get from auth service
    this.programService.completeDay(this.assignmentId, this.dayNumber, userId)
      .subscribe({
        next: () => {
          // Refresh data or navigate
          this.loadExercises();
        },
        error: (err) => console.error(err)
      });
  }
  startWorkout(): void {
    const userId = '680a2319bd2b9864caf53529'; // Get from auth service
    console.log('Starting workout for day:', this.dayNumber);
    console.log('Assignment ID:', this.assignmentId);
    this.programService.startDay(this.assignmentId, this.dayNumber, userId).subscribe({
      next: () => {
        this.dialog.open(WorkoutPlayerComponent, {
          width: '80vw',
          height: '80vh',
          data: {
            exercises: this.dayExercises.exercises,
            assignmentId: this.assignmentId,
            dayNumber: this.dayNumber,
            userId: userId
          }
        });
      }
    });
  }
}