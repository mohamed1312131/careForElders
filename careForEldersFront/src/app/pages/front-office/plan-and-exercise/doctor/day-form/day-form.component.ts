import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProgramService } from '../../ProgramService';


@Component({
  selector: 'app-day-form',
  templateUrl: './day-form.component.html',
  styleUrls: ['./day-form.component.scss']
})
export class DayFormComponent implements OnInit {
  dayForm: FormGroup;
  exercises: any[] = [];
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private programService: ProgramService,
    public dialogRef: MatDialogRef<DayFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.dayForm = this.fb.group({
      dayNumber: [null, [Validators.required, Validators.min(1)]],
      isRestDay: [false],
      warmUpMinutes: [0, [Validators.min(0)]],
      coolDownMinutes: [0, [Validators.min(0)]],
      instructions: [''],
      notesForPatient: [''],
      exerciseIds: [[]]
    });
  }

  ngOnInit(): void {
    this.programService.getAllExercises().subscribe(exercises => {
      this.exercises = exercises;
    });

    if (this.data.day) {
      this.isEditMode = true;
      this.dayForm.patchValue({
        ...this.data.day,
        isRestDay: this.data.day.restDay,
        exerciseIds: this.data.day.exerciseIds
      });
    }
  }

  saveDay() {
    if (this.dayForm.valid) {
      const formValue = this.dayForm.value;
      const dayData = {
        dayNumber: formValue.dayNumber,
        restDay: formValue.isRestDay,
        warmUpMinutes: formValue.warmUpMinutes,
        coolDownMinutes: formValue.coolDownMinutes,
        instructions: formValue.instructions,
        notesForPatient: formValue.notesForPatient,
        exerciseIds: formValue.isRestDay ? [] : formValue.exerciseIds
      };

      this.dialogRef.close(dayData);
    }
  }
}