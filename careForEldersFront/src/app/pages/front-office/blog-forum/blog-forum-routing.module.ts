import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"

import { PostListComponent } from "./post-list/post-list.component"
import { PostFormComponent } from "./post-form/post-form.component"
import { PostDetailComponent } from "./post-detail/post-detail.component"
import { CanDeactivateGuard } from "src/app/guards/can-deactivate.guard"

const routes: Routes = [
  {
    path: "post",
    children: [
      { path: "", component: PostListComponent },
      { path: "create", component: PostFormComponent ,canDeactivate: [CanDeactivateGuard]},
      { path: "edit/:id", component: PostFormComponent,canDeactivate: [CanDeactivateGuard] },
      { path: ":id", component: PostDetailComponent },
    ],
  }
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BlogForumRoutingModule {}