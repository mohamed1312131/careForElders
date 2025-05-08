import { NgModule } from "@angular/core"
import { BrowserModule } from "@angular/platform-browser"
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

@NgModule({
  declarations: [
    AppComponent,
    FullComponent,
    BlankComponent,
    SidebarComponent,
    HeaderComponent,
    BrandingComponent,
    AppNavItemComponent,
    UserinfoComponent
  ],
  imports: [
    BrowserModule,
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


  ],
  exports: [TablerIconsModule],
  bootstrap: [AppComponent],
})
export class AppModule {}
