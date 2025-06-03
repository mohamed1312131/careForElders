import { Component, Inject, OnInit, SecurityContext, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProgramService } from '../../ProgramService'; // Ensure path is correct
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

// Angular Material & Common Modules for Standalone Component
import { CommonModule, DatePipe } from '@angular/common';
import { ApexNonAxisChartSeries, ApexChart, ApexResponsive, ApexLegend, ApexDataLabels, ChartComponent } from 'ng-apexcharts';
import { UserCompComponent } from '../user-comp/user-comp.component';


// Define more specific types for program and patient for better clarity
export interface ExerciseInfo {
  id: string;
  name: string;
  description?: string;
  imageUrl?: string;
  defaultDurationMinutes?: number;
  difficultyLevel?: string;
  equipmentNeeded?: string[]; // This is optional
  videoTutorialUrl?: string;
  safeVideoUrl?: SafeResourceUrl; // For embedded video
}

export interface DayInfo {
  id: string;
  dayNumber: number;
  restDay: boolean;
  warmUpMinutes?: number;
  coolDownMinutes?: number;
  instructions?: string;
  notesForPatient?: string;
  exercises?: ExerciseInfo[];
}

export interface ProgramDetails {
  id: string;
  name: string;
  description?: string;
  programImage?: string;
  programCategory?: string;
  createdBy?: string;
  createdDate?: string | Date;
  updatedDate?: string | Date;
  days?: DayInfo[];
}

export interface PatientInfo {
  patientId: string;
  name: string;
  email?: string;
  assignedDate?: string | Date;
  status?: string;
}
export type ChartOptions = {
  series: ApexNonAxisChartSeries;
  chart: ApexChart;
  responsive: ApexResponsive[];
  labels: string[];
  legend: ApexLegend;
  dataLabels: ApexDataLabels;
  colors: string[];
};

@Component({
  selector: 'app-program-info',
  templateUrl: './program-info.component.html',
  styleUrls: ['./program-info.component.scss'],
  
  providers: [DatePipe]
})
export class ProgramInfoComponent implements OnInit {
  isLoading = true;
  program: ProgramDetails | null = null;
  showChart = false;
  chartData: any;
  patients: PatientInfo[] = [];
  errorLoading = false;
  errorMessage: string = '';
  @ViewChild("chart") chart!: ChartComponent;
  public chartOptions!: Partial<ChartOptions>;

  constructor(
    public dialogRef: MatDialogRef<ProgramInfoComponent>,
    private programService: ProgramService,
    private snackBar: MatSnackBar,
    private sanitizer: DomSanitizer,
     private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: { programId: string }
  ) {
    this.initializeChart();
  }

  ngOnInit(): void {
    if (!this.data.programId) {
      this.handleError('No Program ID provided.');
      return;
    }
    this.loadProgramDetails();
  }

  // Make this method public so it can be called from the template
  public loadProgramDetails(): void {
    this.isLoading = true;
    this.errorLoading = false;
    this.programService.getProgramDetails(this.data.programId).subscribe({
      next: (programData: ProgramDetails) => {
        this.program = {
          ...programData,
          days: programData.days?.map(day => ({
            ...day,
            exercises: day.exercises?.map(ex => ({
              ...ex,
              safeVideoUrl: ex.videoTutorialUrl ? this.getSafeVideoUrl(ex.videoTutorialUrl) : undefined
            }))
          }))
        };
        this.loadPatients();
      },
      error: (err) => {
        console.error("Error loading program details:", err);
        this.handleError('Failed to load program details. Please try again.');
      }
    });
  }

  private loadPatients(): void {
    this.programService.getProgramPatients(this.data.programId).subscribe({
      next: (patientsData: PatientInfo[]) => {
        console.log(patientsData)
        this.patients = patientsData;
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Error loading patients:", err);
        this.snackBar.open('Could not load assigned patients.', 'Close', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  private handleError(message: string): void {
    this.errorMessage = message;
    this.errorLoading = true;
    this.isLoading = false;
    this.snackBar.open(message, 'Close', { duration: 5000, panelClass: ['error-snackbar'] });
  }

  getSafeVideoUrl(url: string): SafeResourceUrl | undefined {
    if (!url) return undefined;
     // Basic check for YouTube URL to attempt to transform to embeddable URL
    // Example: https://www.youtube.com/watch?v=VIDEO_ID -> https://www.youtube.com/embed/VIDEO_ID
    if (url.includes('youtube.com/watch?v=')) {
      const videoId = url.split('v=')[1].split('&')[0]; // Extract video ID
      return this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${videoId}`);
    } else if (url.includes('youtu.be/')) {
      const videoId = url.split('youtu.be/')[1].split('?')[0]; // Extract video ID from short URL
      return this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${videoId}`);
    }
    // For other URLs, sanitize as is, assuming they are direct embed links or other platforms
    // you might want to add more specific parsers here if needed.
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }


  closeDialog(): void {
    this.dialogRef.close();
  }

  private initializeChart(): void {
    this.chartOptions = {
      series: [],
      chart: {
        type: "donut",
        width: 380
      },
      labels: [],
      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              width: 300
            },
            legend: {
              position: "bottom"
            }
          }
        }
      ],
      legend: {
        position: "right",
        fontSize: "14px"
      },
      dataLabels: {
        enabled: true,
        formatter: function (val: number) {
          return val.toFixed(1) + "%";
        }
      },
      colors: ["#4CAF50", "#FFC107", "#F44336"]
    };
  }

  // Add this method to load statistics
  loadProgramStatistics(): void {
    this.programService.getProgramStatistics(this.data.programId).subscribe({
      next: (stats) => {
        console.log("Program Statistics:", stats);
        this.chartData = stats;
        this.updateChart(stats);
        this.showChart = true;
      },
      error: (err) => {
        console.error("Error loading statistics:", err);
        this.snackBar.open('Could not load program statistics', 'Close', { duration: 3000 });
      }
    });
  }

  // Update chart with data
  private updateChart(stats: any): void {
    const total = stats.totalAssignments || 1; // Avoid division by zero
    const completedPercentage = (stats.completedAssignments / total) * 100;
    const activePercentage = (stats.activeAssignments / total) * 100;
    const otherPercentage = 100 - completedPercentage - activePercentage;

    this.chartOptions.series = [
      stats.completedAssignments,
      stats.activeAssignments,
      stats.totalAssignments - stats.completedAssignments - stats.activeAssignments
    ];
    
    this.chartOptions.labels = [
      `Completed (${stats.completedAssignments})`,
      `Active (${stats.activeAssignments})`,
      `Other (${stats.totalAssignments - stats.completedAssignments - stats.activeAssignments})`
    ];
  }

  // Call this from your template to toggle chart visibility
  toggleChart(): void {
    if (!this.showChart) {
      this.loadProgramStatistics();
    } else {
      this.showChart = false;
    }
  }
  openUserDialog(patient: PatientInfo, programId: string): void {
  this.dialog.open(UserCompComponent, {
    width: '800px',
    data: {
      programId,
      patientId: patient.patientId,
      patientName: patient.name,
      patientEmail: patient.email,
      patientStatus: patient.status,
      assignedDate: patient.assignedDate
    }
  });
}
}