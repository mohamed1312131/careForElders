import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProgramService } from '../ProgramService';
import { UserProgramInfoComponent } from '../user-program-info/user-program-info.component';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar'; // Add this for notifications

@Component({
  selector: 'app-plan-list',
  templateUrl: './plan-list.component.html',
  styleUrls: ['./plan-list.component.scss']
})
export class PlanListComponent implements OnInit {
  programs: any[] = [];
  recommendedPrograms: any[] = [];
  userId!: string;
  isLoading = false; // For loading state

  constructor(
    private route: ActivatedRoute,
    private programService: ProgramService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar // For notifications
  ) {}

  ngOnInit(): void {
    this.userId = localStorage.getItem('user_id') ?? '';
    this.loadPrograms();
    this.loadRecommendedPrograms();
  }

  loadPrograms(): void {
    this.programService.getProgramsByUserId(this.userId)
      .subscribe({
        next: (programs) => {
          this.programs = programs;
        },
        error: (err) => {
          console.error('Error loading programs:', err);
          this.showError('Failed to load your programs');
        }
      });
  }

  loadRecommendedPrograms(): void {
    this.programService.getRecommendedPrograms(this.userId)
      .subscribe({
        next: (recommended) => {
          this.recommendedPrograms = recommended;
        },
        error: (err) => {
          console.error('Failed to fetch recommended programs:', err);
          this.showError('Failed to load recommended programs');
        }
      });
  }

  assignProgram(program: any): void {
    this.isLoading = true;
    this.programService.selfAssignProgram(program.id, this.userId)
      .subscribe({
        next: (response) => {
          this.snackBar.open('Program assigned successfully!', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.loadPrograms(); // Refresh the list
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Failed to assign program:', err);
          this.snackBar.open(err.error?.message || 'Failed to assign program', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
          this.isLoading = false;
        }
      });
  }

  openInfo(program: any): void {
    this.dialog.open(UserProgramInfoComponent, {
      width: 'clamp(300px, 70vw, 800px)',
      maxHeight: '90vh',
      data: { programId: program.id },
      panelClass: 'app-dialog-responsive'
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}