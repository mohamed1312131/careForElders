import { Component, ViewChild, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProgramService } from '../../ProgramService'; // Ensure path is correct
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProgramInfoComponent } from '../program-info/program-info.component';
import { DoctorEditPlanComponent } from '../doctor-edit-plan/doctor-edit-plan.component';

// Angular Material Modules
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common'; // Import necessary pipes

export interface ProgramData {
  id: string;
  name: string;
  description: string;
  category: string;
  status: string; // e.g., PUBLISHED, DRAFT, ARCHIVED
  durationWeeks: number;
  createdAt: Date;
  createdBy: string;
  numberOfDays: number;
  numberOfExercises: number;
  numberOfPatients: number;
  totalDuration: number; // Assuming this is in minutes
}

@Component({
  selector: 'app-doctor-plan-list',
  templateUrl: './doctor-plan-list.component.html',
  styleUrls: ['./doctor-plan-list.component.scss'],
  
  providers: [DatePipe, TitleCasePipe] // Provide pipes if not globally available via CommonModule in all contexts
})
export class DoctorPlanListComponent implements AfterViewInit {
  displayedColumns: string[] = [
    'name',
    'category',
    'status',
    'durationWeeks',
    'numberOfDays',
    'numberOfExercises',
    // 'numberOfPatients', // Uncomment if needed, can make table wide
    'totalDuration',
    'createdAt',
    'actions'
  ];

  dataSource!: MatTableDataSource<ProgramData>;
  isLoading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private programService: ProgramService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private changeDetectorRef: ChangeDetectorRef, // For manual change detection if needed
    public titleCasePipe: TitleCasePipe, // Inject for use in template if preferred over direct pipe syntax
    public datePipe: DatePipe // Inject for use in template if preferred
  ) {
    // Data loading moved to ngOnInit for better practice, though constructor can work
  }

  ngOnInit(): void {
    this.loadPrograms();
  }

  ngAfterViewInit() {
    // Ensure dataSource is initialized before assigning paginator and sort
    if (this.dataSource) {
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    }
  }

  loadPrograms(): void {
    this.isLoading = true;
    this.programService.getPrograms().subscribe({
      next: (programs) => {
        this.dataSource = new MatTableDataSource(this.mapPrograms(programs));
        // Important: Assign paginator and sort *after* dataSource is updated
        // and ngAfterViewInit has potentially run.
        if (this.paginator) this.dataSource.paginator = this.paginator;
        if (this.sort) this.dataSource.sort = this.sort;
        
        this.isLoading = false;
        this.changeDetectorRef.detectChanges(); // Trigger change detection if assignments happen late
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Error loading programs', 'Close', { duration: 3000, panelClass: ['warn-snackbar'] });
        this.isLoading = false;
      }
    });
  }

  private mapPrograms(programs: any[]): ProgramData[] {
    return programs.map(program => ({
      id: program.id,
      name: program.name,
      description: program.description,
      category: program.programCategory,
      status: program.status, // Expecting values like 'PUBLISHED', 'DRAFT'
      durationWeeks: program.durationWeeks,
      createdAt: new Date(program.createdDate), // Ensure it's a Date object
      createdBy: program.createdBy,
      numberOfDays: program.numberOfDays,
      numberOfExercises: program.numberOfExercises,
      numberOfPatients: program.numberOfPatients,
      totalDuration: program.totalDurationMinutes
    }));
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  getChipColor(status: string): 'primary' | 'accent' | undefined {
    switch (status?.toUpperCase()) {
      case 'PUBLISHED':
        return 'primary';
      case 'DRAFT':
        return 'accent';
      default: // For 'ARCHIVED' or other statuses
        return undefined; // Uses default Material chip styling (greyish)
    }
  }

  // Formats duration from minutes to a more readable string, e.g., "1h 30m" or "45m"
  formatTotalDuration(minutes: number): string {
    if (isNaN(minutes) || minutes < 0) return 'N/A';
    if (minutes === 0) return '0m';
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    let result = '';
    if (h > 0) result += `${h}h `;
    if (m > 0) result += `${m}m`;
    return result.trim();
  }


  openInfo(program: ProgramData): void {
    this.dialog.open(ProgramInfoComponent, {
      width: 'clamp(300px, 70vw, 800px)', // Responsive width
      maxHeight: '90vh',
      data: { programId: program.id },
      panelClass: 'app-dialog-responsive' // For global responsive dialog styles
    });
  }

  editProgram(program: ProgramData): void {
    const dialogRef = this.dialog.open(DoctorEditPlanComponent, {
      width: 'clamp(400px, 80vw, 900px)',
      maxWidth: '95vw',
      maxHeight: '90vh',
      data: { programId: program.id },
      panelClass: 'app-dialog-responsive'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.updated || result?.assigned) {
        this.snackBar.open('Program data may have changed. Refreshing list...', 'Close', { duration: 2500 });
        this.loadPrograms();
      }
    });
  }

  deleteProgram(programId: string): void {
    // Consider using MatConfirmDialog for better UX than native confirm
    if (confirm('Are you sure you want to delete this program? This action cannot be undone.')) {
      this.isLoading = true; // Show loading indicator during delete
      this.programService.deleteProgram(programId).subscribe({
        next: () => {
          this.snackBar.open('Program deleted successfully', 'Close', { duration: 3000, panelClass: ['success-snackbar'] });
          this.loadPrograms(); // Reload data, which will set isLoading = false
        },
        error: (err) => {
          console.error(err);
          this.snackBar.open('Error deleting program', 'Close', { duration: 3000, panelClass: ['error-snackbar'] });
          this.isLoading = false;
        }
      });
    }
  }

  createNewProgram(): void {
    // This would typically open the DoctorEditPlanComponent with no programId,
    // or a dedicated "create program" component/dialog.
    const dialogRef = this.dialog.open(DoctorEditPlanComponent, {
        width: 'clamp(400px, 80vw, 900px)',
        maxWidth: '95vw',
        maxHeight: '90vh',
        data: { programId: null }, // Indicate creation mode
        panelClass: 'app-dialog-responsive'
    });

    dialogRef.afterClosed().subscribe(result => {
        if (result?.created || result?.updated) { // Assuming 'created' flag for new programs
            this.snackBar.open('Program created/updated. Refreshing list...', 'Close', { duration: 2500 });
            this.loadPrograms();
        }
    });
  }
}