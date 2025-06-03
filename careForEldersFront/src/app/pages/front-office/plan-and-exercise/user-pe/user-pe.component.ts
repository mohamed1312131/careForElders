import { DatePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-pe',
  templateUrl: './user-pe.component.html',
  styleUrls: ['./user-pe.component.scss'],

})
export class UserPEComponent {
  @Input() program!: any;

  constructor(private router: Router) {}

  get daysLeft(): number {
    if (!this.program) return 0;
    const assignedDate = new Date(this.program.assignedDate);
    const today = new Date();
    const diffTime = Math.abs(today.getTime() - assignedDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
    return this.program.totalDays - diffDays;
  }

  navigateToDetails(): void {
    this.router.navigate(['user/userProfile/plan/plandetails', this.program.programId]);
  }
  onImageError(event: Event) {
  (event.target as HTMLImageElement).src = 'assets/default-workout.png';
}
}