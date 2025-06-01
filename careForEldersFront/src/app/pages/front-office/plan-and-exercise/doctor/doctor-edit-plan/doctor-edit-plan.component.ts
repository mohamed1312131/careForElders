// No major changes to TS are strictly required for this UI upgrade,
// assuming the data structures (programDetails, day.exercises, etc.)
// are compatible with the proposed template.
// Ensure ProgramService, DayFormComponent, etc. are correctly imported and standalone if needed.
// Add any new Material modules to the imports array if this component is standalone.

import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms'; // Import ReactiveFormsModule
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog, MatDialogModule } from '@angular/material/dialog'; // Import MatDialogModule
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Import MatSnackBarModule
import { Subscription, Observable, catchError, of, forkJoin } from 'rxjs';
import { ProgramService, ProgramAssignmentDTO } from '../../ProgramService'; // Ensure path is correct
import { DayFormComponent } from '../day-form/day-form.component'; // Ensure DayFormComponent is standalone or its module imported

// Material Modules for standalone component
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCardModule } from '@angular/material/card'; // For exercise display

export interface ProgramEditData {
  programId: string;
}

// You might want to define these interfaces more strictly
// interface ProgramDay { id: string; dayNumber: number; restDay: boolean; warmUpMinutes?: number; coolDownMinutes?: number; instructions?: string; notesForPatient?: string; exercises?: ProgramExercise[]; }
// interface ProgramExercise { id: string; name: string; description?: string; imageUrl?: string; defaultDurationMinutes?: number; }
// interface ProgramDetailsType { id: string; name: string; description: string; programCategory: string; status: string; durationWeeks: number; days: ProgramDay[]; /* ... other fields */ }


@Component({
  selector: 'app-doctor-edit-plan',
  templateUrl: './doctor-edit-plan.component.html',
  styleUrl: './doctor-edit-plan.component.scss',
})
export class DoctorEditPlanComponent implements OnInit, OnDestroy {
  programForm!: FormGroup;
  programDetails: any; // To store full program details (consider defining a strict type)

  allAssignablePatients: any[] = [];
  assignedPatients: any[] = [];

  selectedPatientIdsForAssignment: string[] = [];

  isLoadingProgram = true;
  isLoadingPatients = true;
  isSavingProgram = false;
  isAssigning = false;

  private doctorId!: string;
  private subscriptions = new Subscription();

  constructor(
    public dialogRef: MatDialogRef<DoctorEditPlanComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProgramEditData,
    private fb: FormBuilder,
    private programService: ProgramService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.doctorId = localStorage.getItem('user_id') || ''; // Placeholder for auth
    this.initForm();
    this.loadInitialData();
  }

  private initForm(): void {
    this.programForm = this.fb.group({
      id: [{ value: '', disabled: true }],
      name: ['', Validators.required],
      description: [''],
      programCategory: ['', Validators.required],
      status: ['DRAFT', Validators.required],
      durationWeeks: [null, [Validators.required, Validators.min(1)]],
      programImage: [''] // Example: if you add image upload/link
    });
  }

  private loadInitialData(): void {
    this.isLoadingProgram = true;
    this.isLoadingPatients = true;

    const programDetails$: Observable<any> = this.programService.getProgramDetails(this.data.programId)
      .pipe(catchError(err => {
        this.snackBar.open('Error loading program details.', 'Close', { duration: 3000, panelClass: 'error-snackbar' });
        console.error(err);
        this.isLoadingProgram = false;
        return of(null);
      }));
      
    const allPatients$: Observable<any[]> = this.programService.getAllAssignablePatients()
      .pipe(catchError(err => {
        this.snackBar.open('Error loading assignable patients.', 'Close', { duration: 3000, panelClass: 'error-snackbar' });
        console.error(err);
        return of([]);
      }));

    const assignedPatients$: Observable<any[]> = this.programService.getProgramPatients(this.data.programId)
      .pipe(catchError(err => {
        this.snackBar.open('Error loading assigned patients.', 'Close', { duration: 3000, panelClass: 'error-snackbar' });
        console.error(err);
        return of([]);
      }));

    this.subscriptions.add(
      forkJoin({
        program: programDetails$,
        allPatients: allPatients$,
        assigned: assignedPatients$
      }).subscribe(({ program, allPatients, assigned }) => {
        if (program) {
          this.programDetails = program;
          this.programForm.patchValue({
            id: program.id,
            name: program.name,
            description: program.description,
            programCategory: program.programCategory,
            status: program.status,
            durationWeeks: program.durationWeeks,
            programImage: program.programImage
          });
        }
        this.allAssignablePatients = allPatients;
        this.assignedPatients = assigned;

        this.isLoadingProgram = false;
        this.isLoadingPatients = false;
      })
    );
  }

  getUnassignedPatients(): any[] {
    const assignedIds = new Set(this.assignedPatients.map(p => p.id));
    return this.allAssignablePatients.filter(p => !assignedIds.has(p.id));
  }

  onSaveProgramDetails(): void {
    if (this.programForm.invalid) {
      this.snackBar.open('Please correct the form errors.', 'Close', { duration: 3000 });
      this.programForm.markAllAsTouched();
      return;
    }
    this.isSavingProgram = true;
    const updatedProgramData = this.programForm.getRawValue();

    this.subscriptions.add(
      this.programService.updateProgram(this.data.programId, updatedProgramData, this.doctorId)
        .subscribe({
          next: (response) => {
            this.isSavingProgram = false
            console.log('Program updated successfully:', response);
            this.snackBar.open('Program details updated successfully!', 'Close', { duration: 3000, panelClass: 'success-snackbar' });
            this.dialogRef.close({ updated: true, program: response });
          },
          error: (err) => {
            this.isSavingProgram = false;
            console.error(err);
            this.snackBar.open(err.error?.message || 'Error updating program details.', 'Close', { duration: 5000, panelClass: 'error-snackbar' });
          }
        })
    );
  }

  onAssignSelectedPatients(): void {
    if (this.selectedPatientIdsForAssignment.length === 0) {
      this.snackBar.open('No new patients selected for assignment.', 'Close', { duration: 3000 });
      return;
    }
    this.isAssigning = true;
    const assignmentObservables: Observable<any>[] = this.selectedPatientIdsForAssignment.map(patientId => {
      const assignmentDTO: ProgramAssignmentDTO = {
        programId: this.data.programId,
        patientId: patientId
      };
      return this.programService.assignProgramToPatient(assignmentDTO, this.doctorId).pipe(
        catchError(err => {
          console.error(`Failed to assign program to patient ${patientId}:`, err);
          const patientName = this.getPatientName(patientId) || `Patient ID: ${patientId}`;
          this.snackBar.open(`Error assigning to ${patientName}.`, 'Retry', { duration: 5000, panelClass: 'error-snackbar' });
          return of({ error: true, patientId });
        })
      );
    });

    this.subscriptions.add(
      forkJoin(assignmentObservables).subscribe(results => {
        this.isAssigning = false;
        const successfulAssignments = results.filter(r => !r.error).length;
        const failedAssignments = results.length - successfulAssignments;

        if (successfulAssignments > 0) {
          this.snackBar.open(`${successfulAssignments} patient(s) assigned successfully.`, 'Close', { duration: 3000, panelClass: 'success-snackbar' });
          this.subscriptions.add(this.programService.getProgramPatients(this.data.programId).subscribe(p => this.assignedPatients = p));
          this.selectedPatientIdsForAssignment = [];
        }
        if (failedAssignments > 0) {
          this.snackBar.open(`${failedAssignments} assignment(s) failed. Check console for details.`, 'Close', { duration: 5000, panelClass: 'error-snackbar' });
        }
        if (successfulAssignments > 0 && failedAssignments === 0) {
          this.dialogRef.close({ assigned: true });
        } else if (successfulAssignments > 0) {
             // Partial success, might still want to indicate something changed if the parent component needs to refresh
             this.dialogRef.close({ assigned: true, partial: true });
        }
      })
    );
  }

  getPatientName(patientId: string): string | undefined {
      const patient = this.allAssignablePatients.find(p => p.id === patientId) || this.assignedPatients.find(p => p.id === patientId);
      return patient ? patient.name : undefined;
  }
  
  // Method to unassign a patient (example, not fully implemented in UI yet)
  onUnassignPatient(patientId: string): void {
    if (!confirm(`Are you sure you want to unassign ${this.getPatientName(patientId) || 'this patient'}?`)) {
      return;
    }
    // Add service call to unassign patient
    // this.programService.unassignProgramFromPatient(this.data.programId, patientId, this.doctorId).subscribe(...)
    console.log('Unassign patient:', patientId);
    this.snackBar.open('Unassign feature not fully implemented.', 'Close', {duration: 3000});
  }


  closeDialog(result?: any): void {
    this.dialogRef.close(result);
  }

  openDayForm(day?: any): void {
  const dialogRef = this.dialog.open(DayFormComponent, {
    width: 'clamp(500px, 70vw, 750px)',
    maxWidth: '95vw',
    data: {
      programId: this.data.programId,
      day: day ? { ...day } : null,
      doctorId: this.doctorId,
      existingDayNumbers: (this.programDetails?.days || []).map((d: any) => d.dayNumber)
    },
    disableClose: true
  });

  dialogRef.afterClosed().subscribe(response => {
    if (response && response.day && response.action) {
      this.snackBar.open(
        `Day ${response.day.dayNumber} ${response.action} successfully.`,
        'Close',
        { duration: 3000, panelClass: 'success-snackbar' }
      );
      // 🔄 Reload full data from backend to ensure sync
      this.loadInitialData();
    } else if (response) {
      console.log('Day form closed with an unexpected response or no action:', response);
    }
  });
}



  // addDay and updateDay methods are now effectively handled by openDayForm and its afterClosed logic.
  // The DayFormComponent should handle the actual API call for add/update.
  // The main component (DoctorEditPlanComponent) just needs to update its local programDetails.days array.

  deleteDay(dayToDelete: any): void {
    if (confirm(`Are you sure you want to delete Day ${dayToDelete.dayNumber}? This action cannot be undone.`)) {
      this.programService.deleteProgramDay(this.data.programId, dayToDelete.id, this.doctorId)
        .subscribe({
          next: () => {
            this.programDetails.days = this.programDetails.days.filter((d: any) => d.id !== dayToDelete.id);
            this.snackBar.open('Day deleted successfully', 'Close', { duration: 3000, panelClass: 'success-snackbar' });
          },
          error: (err) => {
            console.error(err);
            this.snackBar.open('Error deleting day: ' + err.error?.message, 'Close', { duration: 5000, panelClass: 'error-snackbar' });
          }
        });
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}