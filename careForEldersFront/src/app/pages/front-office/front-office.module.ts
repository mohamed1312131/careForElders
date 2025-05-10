import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FrontOfficeRoutes } from './front-office.routing';
import { RouterModule } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';

@NgModule({
  declarations: [
    HomePageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(FrontOfficeRoutes),
    MatDatepickerModule
  ],
  providers: [
    provideNativeDateAdapter()  // Add this line to provide the DateAdapter
  ]
})
export class FrontOfficeModule { }
