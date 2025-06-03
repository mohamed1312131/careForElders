import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlanListComponent } from './plan-list/plan-list.component';
import { PlanDetailsComponent } from './plan-details/plan-details.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';

const routes: Routes = [
  { path: '', component: PlanListComponent },
  { path: 'plan-details/:id', component: PlanDetailsComponent },
  { path: 'admin', component: AdminDashboardComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes )],
  exports: [RouterModule]
})
export class NutritionRoutingModule { }
