// plan-details.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgramService } from '../ProgramService';



@Component({
  selector: 'app-plan-details',
  templateUrl: './plan-details.component.html',
  styleUrls: ['./plan-details.component.scss'],
})
export class PlanDetailsComponent implements OnInit {
  programDetails!: any;
  patientId!: string;

  constructor(
    private route: ActivatedRoute,
    private programService: ProgramService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const programId = this.route.snapshot.paramMap.get('programId')!;
    this.patientId = '680a2319bd2b9864caf53529'; // Get from auth service
    
    this.programService.getProgramDetailsWithProgress(programId, this.patientId)
      .subscribe({
        next: (details) => this.programDetails = details,
        complete: () => console.log('Program details loaded successfully', this.programDetails),
        error: (err) => console.error(err)
      });
  }
  startDay(dayNumber: number): void {
    console.log('Starting day:', dayNumber);
    console.log('Program details:', this.programDetails.assignmentId);
    if (!this.programDetails?.assignmentId) return;
    
    this.router.navigate([
      'plan/program',
      this.programDetails.assignmentId,
      'day',
      dayNumber
    ]);
  }
  getStatusClass(status: string): string {
    return status.toLowerCase().replace('_', '-');
  }
  
}