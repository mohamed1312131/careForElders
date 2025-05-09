import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TestingComponent } from './testing/testing.component';
import { SearchDoctorComponent } from './search-doctor/search-doctor.component';

const routes: Routes = [
  {path:'search', component:SearchDoctorComponent},
  
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AppointmentAvailabilityRoutingModule { }
