import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { BlankComponent } from "./layouts/blank/blank.component"
import { FullComponent } from "./layouts/full/full.component"
import { BillListComponent } from "./billing/billing/bill-list/bill-list.component"
import { BillFormComponent } from "./billing/billing/bill-form/bill-form.component"
import { BillDetailComponent } from "./billing/billing/bill-details/bill-details.component"

const routes: Routes = [
  // Public front office routes
  {
    path: "",
    loadChildren: () => import("./pages/front-office/front-office.module").then((m) => m.FrontOfficeModule),
  },

  // Billing routes - nested under the FullComponent layout
  {
    path: "billing",
    component: FullComponent,
    children: [
      { path: "", redirectTo: "bills", pathMatch: "full" },
      { path: "bills", component: BillListComponent },
      { path: "bills/new", component: BillFormComponent },
      { path: "bills/:id", component: BillDetailComponent },
      { path: "bills/:id/edit", component: BillFormComponent },
    ],
  },

  // Admin authentication routes (login/register)
  {
    path: "admin/authentication",
    component: BlankComponent,
    children: [
      { path: "", redirectTo: "login", pathMatch: "full" },
      {
        path: "login",
        loadChildren: () => import("./pages/authentication/authentication.module").then((m) => m.AuthenticationModule),
      },
      { path: "**", redirectTo: "login" },
    ],
  },

  // Admin dashboard routes
  {
    path: "admin",
    component: FullComponent,
    children: [
      { path: "", redirectTo: "dashboard", pathMatch: "full" },
      { path: "dashboard", loadChildren: () => import("./pages/pages.module").then((m) => m.PagesModule) },
      {
        path: "ui-components",
        loadChildren: () => import("./pages/ui-components/ui-components.module").then((m) => m.UicomponentsModule),
      },
      { path: "extra", loadChildren: () => import("./pages/extra/extra.module").then((m) => m.ExtraModule) },
    ],
  },

  // Global catch-all route
  { path: "**", redirectTo: "" },
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
