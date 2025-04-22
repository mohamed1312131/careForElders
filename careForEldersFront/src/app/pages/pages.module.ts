import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PagesRoutes } from './pages.routing.module';
import { MaterialModule } from '../material.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { NgApexchartsModule } from 'ng-apexcharts';
// icons
import { TablerIconsModule } from 'angular-tabler-icons';
import * as TablerIcons from 'angular-tabler-icons/icons';
import { AppDashboardComponent } from './dashboard/dashboard.component';
import {ConfirmDialogComponent} from "./dashboard/confirm-dialog/confirm-dialog.component";
import {UserDialogComponent} from "./dashboard/user-dialog/user-dialog.component";
import { MatChipsModule } from '@angular/material/chips';
import {MatDialogModule} from "@angular/material/dialog";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ToastrModule} from "ngx-toastr";
@NgModule({
  declarations: [AppDashboardComponent,
    UserDialogComponent,
    ConfirmDialogComponent],
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    NgApexchartsModule,
    RouterModule.forChild(PagesRoutes),
    TablerIconsModule.pick(TablerIcons),
    MatChipsModule,
    ReactiveFormsModule,
    MatChipsModule,
    MatDialogModule,

  ],
  exports: [TablerIconsModule],
})
export class PagesModule {}
