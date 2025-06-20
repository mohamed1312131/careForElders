import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription, Observable, catchError, of, forkJoin } from 'rxjs';
import { ProgramService, ProgramAssignmentDTO } from '../../ProgramService';
import { DayFormComponent } from '../day-form/day-form.component';
export interface ProgramEditData {
  programId: string;
  // You can pass the full program object if you have it to avoid an initial fetch
  // program?: any; 
}
@Component({
  selector: 'app-doctor-edit-plan',
  templateUrl: './doctor-edit-plan.component.html',
  styleUrl: './doctor-edit-plan.component.scss'
})
export class DoctorEditPlanComponent implements OnInit, OnDestroy {
  programForm!: FormGroup;
  programDetails: any; // To store full program details

  allAssignablePatients: any[] = [];
  assignedPatients: any[] = []; // Patients currently assigned to this program

  // Store IDs of patients selected in the dropdown for new assignment
  selectedPatientIdsForAssignment: string[] = []; 

  isLoadingProgram = true;
  isLoadingPatients = true;
  isSavingProgram = false;
  isAssigning = false;

  private doctorId! :string; // TODO: Get from auth service
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
    this.doctorId = localStorage.getItem('user_id') || ''; // TODO: Get from auth service
    console.log('Doctor ID:', this.doctorId);
    this.initForm();
    this.loadInitialData();
  }

  private initForm(): void {
    this.programForm = this.fb.group({
      id: [{ value: '', disabled: true }],
      name: ['', Validators.required],
      description: [''],
      programCategory: ['', Validators.required], // Matched ProgramInfoComponent
      status: ['DRAFT', Validators.required], // Default or fetched
      durationWeeks: [null, [Validators.required, Validators.min(1)]],
      // Add other editable fields from your Program entity/DTO
      // Example: programImage: ['']
    });
  }

  private loadInitialData(): void {
    this.isLoadingProgram = true;
    this.isLoadingPatients = true;

    const programDetails$: Observable<any> = this.programService.getProgramDetails(this.data.programId)
      .pipe(catchError(err => {
        this.snackBar.open('Error loading program details.', 'Close', { duration: 3000 });
        console.error(err);
        this.isLoadingProgram = false;
        return of(null);
      }));

    const allPatients$: Observable<any[]> = this.programService.getAllAssignablePatients()
      .pipe(catchError(err => {
        this.snackBar.open('Error loading assignable patients.', 'Close', { duration: 3000 });
        console.error(err);
        return of([]);
      }));

    const assignedPatients$: Observable<any[]> = this.programService.getProgramPatients(this.data.programId)
      .pipe(catchError(err => {
        this.snackBar.open('Error loading assigned patients.', 'Close', { duration: 3000 });
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
            programCategory: program.programCategory, // Ensure this key matches your DTO
            status: program.status,
            durationWeeks: program.durationWeeks,
            // programImage: program.programImage
          });
        }
        this.allAssignablePatients = allPatients;
        this.assignedPatients = assigned;

        this.isLoadingProgram = false;
        this.isLoadingPatients = false;
      })
    );
  }

  // Filter out already assigned patients from the main selection dropdown
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
    const updatedProgramData = this.programForm.getRawValue(); // getRawValue includes disabled fields like ID

    this.subscriptions.add(
      this.programService.updateProgram(this.data.programId, updatedProgramData, this.doctorId)
        .subscribe({
          next: (response) => {
            this.isSavingProgram = false;
            this.snackBar.open('Program details updated successfully!', 'Close', { duration: 3000 });
            this.dialogRef.close({ updated: true, program: response }); // Pass some data back
          },
          error: (err) => {
            this.isSavingProgram = false;
            console.error(err);
            this.snackBar.open(err.error?.message || 'Error updating program details.', 'Close', { duration: 3000 });
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
          this.snackBar.open(`Error assigning to patient ${this.getPatientName(patientId)}.`, 'Retry', { duration: 5000 });
          return of({ error: true, patientId }); // Propagate error to handle partial success
        })
      );
    });

    this.subscriptions.add(
      forkJoin(assignmentObservables).subscribe(results => {
        this.isAssigning = false;
        const successfulAssignments = results.filter(r => !r.error).length;
        const failedAssignments = results.length - successfulAssignments;

        if (successfulAssignments > 0) {
          this.snackBar.open(`${successfulAssignments} patient(s) assigned successfully.`, 'Close', { duration: 3000 });
           // Refresh assigned patients list
          this.subscriptions.add(this.programService.getProgramPatients(this.data.programId).subscribe(p => this.assignedPatients = p));
          this.selectedPatientIdsForAssignment = []; // Clear selection
        }
        if (failedAssignments > 0) {
          this.snackBar.open(`${failedAssignments} assignment(s) failed. Check console for details.`, 'Close', { duration: 5000 });
        }
        if (successfulAssignments > 0 && failedAssignments === 0) {
             this.dialogRef.close({ assigned: true }); // Indicate assignment happened
        }
      })
    );
  }

  getPatientName(patientId: string): string {
      const patient = this.allAssignablePatients.find(p => p.id === patientId);
      return patient ? patient.name : patientId;
  }

  closeDialog(result?: any): void {
    this.dialogRef.close(result);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
  

 openDayForm(day?: any): void {
    const dialogRef = this.dialog.open(DayFormComponent, {
      width: '600px',
      data: { 
        programId: this.data.programId, 
        day: day,
        doctorId: this.doctorId
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (day) {
          this.updateDay(day.id, result);
        } else {
          this.addDay(result);
        }
      }
    });
  }

  addDay(dayData: any): void {
    this.programService.addDayToProgram(this.data.programId, dayData, this.doctorId)
      .subscribe({
        next: (updatedProgram) => {
          this.programDetails = updatedProgram;
          this.snackBar.open('Day added successfully', 'Close', { duration: 3000 });
        },
        error: (err) => {
          console.error(err);
          this.snackBar.open('Error adding day: ' + err.error?.message, 'Close', { duration: 5000 });
        }
      });
  }

  updateDay(dayId: string, dayData: any): void {
    this.programService.updateProgramDay(this.data.programId, dayId, dayData, this.doctorId)
      .subscribe({
        next: (updatedDay) => {
          const index = this.programDetails.days.findIndex((d: any) => d.id === dayId);
          if (index > -1) {
            this.programDetails.days[index] = updatedDay;
          }
          this.snackBar.open('Day updated successfully', 'Close', { duration: 3000 });
        },
        error: (err) => {
          console.error(err);
          this.snackBar.open('Error updating day: ' + err.error?.message, 'Close', { duration: 5000 });
        }
      });
  }

  deleteDay(day: any): void {
    if (confirm(`Are you sure you want to delete Day ${day.dayNumber}? This action cannot be undone.`)) {
      this.programService.deleteProgramDay(this.data.programId, day.id, this.doctorId)
        .subscribe({
          next: () => {
            this.programDetails.days = this.programDetails.days.filter((d: any) => d.id !== day.id);
            this.snackBar.open('Day deleted successfully', 'Close', { duration: 3000 });
          },
          error: (err) => {
            console.error(err);
            this.snackBar.open('Error deleting day: ' + err.error?.message, 'Close', { duration: 5000 });
          }
        });
    }
  }
}