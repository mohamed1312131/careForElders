import { NgModule } from "@angular/core"
import { BrowserModule } from "@angular/platform-browser"
import { BrowserAnimationsModule } from "@angular/platform-browser/animations"
import { HttpClientModule } from "@angular/common/http"

import { AppRoutingModule } from "./app-routing.module"
import { AppComponent } from "./app.component"

// icons
import { TablerIconsModule } from "angular-tabler-icons"
import * as TablerIcons from "angular-tabler-icons/icons"

//Import all material modules
import { MaterialModule } from "./material.module"
import { FormsModule, ReactiveFormsModule } from "@angular/forms"

//Import Layouts
import { FullComponent } from "./layouts/full/full.component"
import { BlankComponent } from "./layouts/blank/blank.component"

// Vertical Layout
import { SidebarComponent } from "./layouts/full/sidebar/sidebar.component"
import { HeaderComponent } from "./layouts/full/header/header.component"
import { BrandingComponent } from "./layouts/full/sidebar/branding.component"
import { AppNavItemComponent } from "./layouts/full/sidebar/nav-item/nav-item.component"


// Services
import { BillService } from "./services/bill.service"
// Billing Components
import { BillDetailComponent } from "./billing/billing/bill-details/bill-details.component"
import { BillFormComponent } from "./billing/billing/bill-form/bill-form.component"
import { BillListComponent } from "./billing/billing/bill-list/bill-list.component"

@NgModule({
  declarations: [
    AppComponent,
    FullComponent,
    BlankComponent,
    SidebarComponent,
    HeaderComponent,
    BrandingComponent,
    AppNavItemComponent,
    // Billing Components
    BillListComponent,
    BillDetailComponent,
    BillFormComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    TablerIconsModule.pick(TablerIcons),
  ],
  providers: [
    BillService, // Explicitly provide the BillService
  ],
  exports: [TablerIconsModule],
  bootstrap: [AppComponent],
})
export class AppModule {}
