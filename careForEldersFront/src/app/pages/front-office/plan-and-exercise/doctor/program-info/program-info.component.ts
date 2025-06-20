import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { MatSnackBar } from '@angular/material/snack-bar';
import { ProgramService } from '../../ProgramService';

@Component({
  selector: 'app-program-info',
  templateUrl: './program-info.component.html',
  styleUrls: ['./program-info.component.scss']
})
export class ProgramInfoComponent implements OnInit {
  isLoading = true;
  program: any;
  patients: any[] = [];
  errorLoading = false;

  constructor(
    public dialogRef: MatDialogRef<ProgramInfoComponent>,
    private programService: ProgramService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { programId: string }
  ) {}

  ngOnInit(): void {
    this.loadProgramDetails();
  }

  private loadProgramDetails(): void {
    this.programService.getProgramDetails(this.data.programId).subscribe({
      next: (program) => {
        this.program = program;
        this.loadPatients();
      },
      error: (err) => {
        console.error(err);
        this.errorLoading = true;
        this.isLoading = false;
        this.snackBar.open('Error loading program details', 'Close', { duration: 3000 });
      }
    });
  }

  private loadPatients(): void {
    this.programService.getProgramPatients(this.data.programId).subscribe({
      next: (patients) => {
        this.patients = patients;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        this.snackBar.open('Error loading patients', 'Close', { duration: 3000 });
      }
    });
  }

  close() {
    this.dialogRef.close();
  }
}