import { NgModule } from "@angular/core"
import { BrowserAnimationsModule } from "@angular/platform-browser/animations"
import { HttpClientModule } from "@angular/common/http"

import { AppRoutingModule } from "./app-routing.module"
import { AppComponent } from "./app.component"

// icons
import { TablerIconsModule } from "angular-tabler-icons"
import * as TablerIcons from "angular-tabler-icons/icons"

// Import all material modules
import { MaterialModule } from './material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Import Layouts
import { FullComponent } from './layouts/full/full.component';
import { BlankComponent } from './layouts/blank/blank.component';

// Vertical Layout
import { SidebarComponent } from "./layouts/full/sidebar/sidebar.component"
import { HeaderComponent } from "./layouts/full/header/header.component"
import { BrandingComponent } from "./layouts/full/sidebar/branding.component"
import { AppNavItemComponent } from "./layouts/full/sidebar/nav-item/nav-item.component"
import { FrontOfficeModule } from "./pages/front-office/front-office.module"
//import { ApiTestComponent } from "./test/api-test.component"



// ToastrModule for Toastr notifications
import { ToastrModule } from 'ngx-toastr';
import { UserinfoComponent } from './pages/front-office/user-service/userinfo/userinfo.component';

import {MedicalRecordComponent} from "./pages/front-office/medical-record/medical-record/medical-record.component";
import {AddNoteDialogComponent} from "./pages/front-office/medical-record/add-note-dialog/add-note-dialog.component";
import {TruncatePipe} from "./pages/front-office/medical-record/truncate.pipe";
import {
  DocumentPreviewDialogComponent
} from "./pages/front-office/medical-record/document-preview-dialog/document-preview-dialog.component";
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatChipsModule } from '@angular/material/chips';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTabsModule } from '@angular/material/tabs';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {PlanListComponent} from "./pages/front-office/nutrition/plan-list/plan-list.component";
import {PlanDetailsComponent} from "./pages/front-office/nutrition/plan-details/plan-details.component";
import {AdminDashboardComponent} from "./pages/front-office/nutrition/admin-dashboard/admin-dashboard.component";
import { CommonModule } from "@angular/common"

@NgModule({
  declarations: [
    AppComponent,
    FullComponent,
    BlankComponent,
    SidebarComponent,
    HeaderComponent,
    BrandingComponent,
    AppNavItemComponent,
    UserinfoComponent,
    MedicalRecordComponent,
    AddNoteDialogComponent,
    TruncatePipe,
    DocumentPreviewDialogComponent,
    UserinfoComponent,
    PlanListComponent,
    PlanDetailsComponent,
    AdminDashboardComponent,
  ],
  imports: [
    CommonModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule, // Required for animations
    FormsModule,
    ReactiveFormsModule,

    MaterialModule,
    TablerIconsModule.pick(TablerIcons),
    ToastrModule.forRoot({
      positionClass: 'toast-top-right', // Top-right position for toasts
      closeButton: true,              // Include a close button for user convenience
      timeOut: 5000,                  // Set toast duration to 5 seconds
      progressBar: true,              // Enable progress bar
      preventDuplicates: true,        // Prevent duplicate toasts
      newestOnTop: true,              // Ensure the most recent toast is on top
    }),
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatListModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule,
    MatProgressBarModule,
    MatPaginatorModule,
    MatExpansionModule,
    MatChipsModule,
    MatBadgeModule,
    MatTabsModule,
  
  ],
  exports: [TablerIconsModule],
  bootstrap: [AppComponent],
})
export class AppModule {}
