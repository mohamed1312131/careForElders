import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { formatDate as angularFormatDate } from '@angular/common';
import { ProgramService } from '../../ProgramService';
export interface DayStatus {
  completed: boolean;
  completionDate?: { $date: string };
  actualDurationMinutes?: number;
  actualStartDateTime?: { $date: string };
  completedExerciseIds?: string[];
  perceivedDifficulty?: number;
  coachComments?: string;
}

export interface PatientProgramAssignment {
  _id: { $oid: string };
  patientId: string;
  programId: string;
  assignedDate: { $date: string };
  completionPercentage: number;
  dayStatuses: { [day: string]: DayStatus };
  actualEndDate?: { $date: string };
  status: 'COMPLETED' | 'IN_PROGRESS' | 'NOT_STARTED' | 'CANCELLED';
  currentDay: number;
  lastActivityDate: { $date: string };
  isDeleted: boolean;
  _class: string;

  // Optionally populated on the frontend
  programName?: string;
  patientName?: string;
}
export interface TransformedDayStatus extends DayStatus {
  day: number;
  completionDateFormatted?: string;
  actualStartDateTimeFormatted?: string;
}
@Component({
  selector: 'app-user-comp',
  templateUrl: './user-comp.component.html',
  styleUrls: ['./user-comp.component.scss']
})
export class UserCompComponent implements OnInit {
  programDetails: PatientProgramAssignment | null = null;
  transformedDayStatuses: TransformedDayStatus[] = [];
  isLoading = true;
  errorLoading = false;

  constructor(
    public dialogRef: MatDialogRef<UserCompComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      programId: string;
      patientId: string;
      patientName?: string;
    },
    private programService: ProgramService
  ) {}

  ngOnInit(): void {
    this.fetchProgramDetails();
  }

  fetchProgramDetails(): void {
    this.isLoading = true;
    this.errorLoading = false;
    console.log("patient data",this.data);
    this.programService.getPatientAssignmentDetails(this.data.programId, this.data.patientId)
      .subscribe({
        next: (res: PatientProgramAssignment) => {
          console.log("Program details fetched:", res);
          this.programDetails = {
            ...res,
            patientName: this.data.patientName
          };
          this.transformedDayStatuses = this.transformDayStatuses(res.dayStatuses);
          this.isLoading = false;
        },
        error: () => {
          this.errorLoading = true;
          this.isLoading = false;
        }
      });
  }

  transformDayStatuses(dayStatuses: { [key: string]: DayStatus }): TransformedDayStatus[] {
    return Object.entries(dayStatuses).map(([day, status]) => ({
      ...status,
      day: +day,
      completionDateFormatted: this.formatDate(status.completionDate?.$date),
      actualStartDateTimeFormatted: this.formatDate(status.actualStartDateTime?.$date)
    })).sort((a, b) => a.day - b.day);
  }

  formatDate(dateString?: string): string {
    return dateString ? angularFormatDate(dateString, 'medium', 'en-US') : 'N/A';
  }

  getOverallStatusChipClass(status: string): string {
    switch (status) {
      case 'COMPLETED': return 'status-completed';
      case 'IN_PROGRESS': return 'status-in-progress';
      case 'NOT_STARTED': return 'status-not-started';
      case 'CANCELLED': return 'status-cancelled';
      default: return 'status-unknown';
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
