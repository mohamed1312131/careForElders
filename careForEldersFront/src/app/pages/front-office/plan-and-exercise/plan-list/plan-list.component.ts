import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProgramService } from '../ProgramService';


@Component({
  selector: 'app-plan-list',
  templateUrl: './plan-list.component.html',
  styleUrls: ['./plan-list.component.scss']
})
export class PlanListComponent implements OnInit {
  programs: any[] = [];
  userId!: string;

  constructor(
    private route: ActivatedRoute,
    private programService: ProgramService
  ) {}

  ngOnInit(): void {
    this.userId = localStorage.getItem('user_id') ?? '';
    this.loadPrograms();
  }

  loadPrograms(): void {
    this.programService.getProgramsByUserId(this.userId)
      .subscribe({

        next: (programs) => {this.programs = programs,console.log('Programs loaded:', programs)},

        next: (programs) => this.programs = programs,
 ebe25a6 (change)
        error: (err) => console.error('Error loading programs:', err)
      });
  }
}