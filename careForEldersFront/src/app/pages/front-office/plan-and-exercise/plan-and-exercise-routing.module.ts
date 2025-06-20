import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserPEComponent } from './user-pe/user-pe.component';
import { PlanListComponent } from './plan-list/plan-list.component';
import { PlanDetailsComponent } from './plan-details/plan-details.component';
import { ProgramComponent } from './program/program.component';
import { DoctorPlanListComponent } from './doctor/doctor-plan-list/doctor-plan-list.component';
import { DoctorAddProgramComponent } from './doctor/doctor-add-program/doctor-add-program.component';
import { AddExerciseComponent } from './doctor/add-exercise/add-exercise.component';

const routes: Routes = [
  {
    path: 'userprogram/:userId',
    component: PlanListComponent,
  },
  {
    path: 'plandetails/:programId',
    component: PlanDetailsComponent,
  },
  {
    path:'program/:assignmentId/day/:dayNumber',
    component: ProgramComponent,
  },
  {
    path:'programList/:doctorId',
    component: DoctorPlanListComponent,
  },
  {
    path:'addProgram',
    component: DoctorAddProgramComponent,
  },
  {
    path:'addExercise/:doctorId',
    component: AddExerciseComponent,
  },
  {
    path: 'nutrition',
    loadChildren: () => import('../nutrition/nutrition.module').then(m => m.NutritionModule)
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PlanAndExerciseRoutingModule { }
