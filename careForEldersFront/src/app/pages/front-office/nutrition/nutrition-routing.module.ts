import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { PlanDetailsComponent } from './plan-details/plan-details.component';
import { PlanListComponent } from './plan-list/plan-list.component';
import { NutritionPlanGeneratorComponent } from './nutrition-plan-generator/nutrition-plan-generator.component';

const routes: Routes = [
  { path: 'admin', component: AdminDashboardComponent },
  { path: 'list', component: PlanListComponent },
  { path: 'details/:id', component: PlanDetailsComponent },
  { path: 'plan-generator', component: NutritionPlanGeneratorComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NutritionRoutingModule { }
