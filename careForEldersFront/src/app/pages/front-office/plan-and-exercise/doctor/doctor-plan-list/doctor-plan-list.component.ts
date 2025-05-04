import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProgramService } from '../../ProgramService';
import { MatDialog } from '@angular/material/dialog';
import { ProgramInfoComponent } from '../program-info/program-info.component';

export interface ProgramData {
  id: string;
  name: string;
  description: string;
  category: string;
  status: string;
  durationWeeks: number;
  createdAt: Date;
  createdBy: string;
  numberOfDays: number;
  numberOfExercises: number;
  numberOfPatients: number;
  totalDuration: number;
}

@Component({
  selector: 'app-doctor-plan-list',
  templateUrl: './doctor-plan-list.component.html',
  styleUrls: ['./doctor-plan-list.component.scss']
})
export class DoctorPlanListComponent implements AfterViewInit {
  displayedColumns: string[] = [
    'name', 
    'category', 
    'status', 
    'durationWeeks',
    'numberOfDays',
    'numberOfExercises',
    'numberOfPatients',
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
    private dialog: MatDialog
  ) {
    this.loadPrograms();
  }

  ngAfterViewInit() {
    if (this.dataSource) {
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    }
  }

  loadPrograms(): void {
    this.programService.getPrograms().subscribe({
      next: (programs) => {
        console.log(programs);
        this.dataSource = new MatTableDataSource(this.mapPrograms(programs));
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Error loading programs', 'Close', { duration: 3000 });
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
      status: program.status,
      durationWeeks: program.durationWeeks,
      createdAt: program.createdDate,
      createdBy: program.createdBy,
      numberOfDays: program.numberOfDays,
      numberOfExercises: program.numberOfExercises,
      numberOfPatients: program.numberOfPatients,
      totalDuration: program.totalDurationMinutes
    }));
  }

  // ... rest of the component remains the same


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  deleteProgram(programId: string): void {
    if (confirm('Are you sure you want to delete this program?')) {
      this.programService.deleteProgram(programId).subscribe({
        next: () => {
          this.loadPrograms();
          this.snackBar.open('Program deleted successfully', 'Close', { duration: 3000 });
        },
        error: (err) => {
          console.error(err);
          this.snackBar.open('Error deleting program', 'Close', { duration: 3000 });
        }
      });
    }
  }
  viewProgram(program: ProgramData): void {
    console.log('Viewing program:', program);
    // You can open a dialog or navigate to a details page
  }
  
  editProgram(program: ProgramData): void {
    console.log('Editing program:', program);
    // You can open a form pre-filled with program data
  }
  openInfo(program: ProgramData): void {
    this.dialog.open(ProgramInfoComponent, {
      width: '800px',
      maxHeight: '90vh',
      data: { programId: program.id }
    });
  }
}