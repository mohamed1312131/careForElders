import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PlanAndExerciseRoutingModule } from './plan-and-exercise-routing.module';
import { UserPEComponent } from './user-pe/user-pe.component';
import { MaterialModule } from 'src/app/material.module';
import { PlanListComponent } from './plan-list/plan-list.component';
import { PlanDetailsComponent } from './plan-details/plan-details.component';
import { ProgramComponent } from './program/program.component';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { WorkoutPlayerComponent } from './workout-player/workout-player.component';
import { DoctorPlanListComponent } from './doctor/doctor-plan-list/doctor-plan-list.component';
import { DoctorAddProgramComponent } from './doctor/doctor-add-program/doctor-add-program.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProgramInfoComponent } from './doctor/program-info/program-info.component';
import { AddExerciseComponent } from './doctor/add-exercise/add-exercise.component';
import { DoctorEditPlanComponent } from './doctor/doctor-edit-plan/doctor-edit-plan.component';
import { MatDialogModule } from '@angular/material/dialog';

import {MatChipsModule} from '@angular/material/chips';
import { DayFormComponent } from './doctor/day-form/day-form.component';
import { UserProgramInfoComponent } from './user-program-info/user-program-info.component';
import { NgApexchartsModule } from 'ng-apexcharts';
import { UserCompComponent } from './doctor/user-comp/user-comp.component';
@NgModule({
  declarations: [
    UserPEComponent,
    PlanListComponent,
    PlanDetailsComponent,
    ProgramComponent,
    WorkoutPlayerComponent,
    DoctorPlanListComponent,
    DoctorAddProgramComponent,
    ProgramInfoComponent,
    AddExerciseComponent,
    DoctorEditPlanComponent,
    DayFormComponent,
    UserProgramInfoComponent,
    UserCompComponent
  ],
  imports: [
    CommonModule,
    PlanAndExerciseRoutingModule,
    MaterialModule,
    HttpClientModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MatChipsModule,
    MatDialogModule,
    NgApexchartsModule
    
  ],
  exports: [
    UserPEComponent,
    PlanListComponent,
    PlanDetailsComponent,
    ProgramComponent,
    WorkoutPlayerComponent,
    DoctorPlanListComponent,
    DoctorAddProgramComponent,
    ProgramInfoComponent,
    AddExerciseComponent,
  
  ]
})
export class PlanAndExerciseModule { }
