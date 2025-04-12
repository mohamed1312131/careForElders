import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FrontOfficeRoutes } from './front-office.routing';
import { RouterModule } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';


@NgModule({
  declarations: [
    HomePageComponent,

  ],
  imports: [
    CommonModule,
    RouterModule.forChild(FrontOfficeRoutes),

  ]
})
export class FrontOfficeModule { }
