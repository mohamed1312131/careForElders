import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { BlankComponent } from "./layouts/blank/blank.component"
import { FullComponent } from "./layouts/full/full.component"
//import { ApiTestComponent } from "./test/api-test.component"

const routes: Routes = [
  // Public front office routes
  {
    path: "",
    loadChildren: () => import("./pages/front-office/front-office.module").then((m) => m.FrontOfficeModule),
  },

  // Billing routes - nested under the FullComponent layout
  //{ path: "api-test", component: ApiTestComponent },
  // Add a redirect to the test page for easy access
  { path: "test", redirectTo: "api-test", pathMatch: "full" },

  {
    path: 'patient-bills',
    loadChildren: () => import('./pages/front-office/patient-bill/patient-bills.module').then(m => m.PatientBillsModule)
  },
  { path: '', redirectTo: 'patient-bills', pathMatch: 'full' },
  { path: '**', redirectTo: 'patient-bills' },
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
