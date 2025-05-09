import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"

import { PostListComponent } from "./post-list/post-list.component"
import { PostFormComponent } from "./post-form/post-form.component"
import { PostDetailComponent } from "./post-detail/post-detail.component"

const routes: Routes = [
  {
    path: "post",
    children: [
      { path: "", component: PostListComponent },
      { path: "create", component: PostFormComponent },
      { path: "edit/:id", component: PostFormComponent },
      { path: ":id", component: PostDetailComponent },
    ],
  },
  { path: "", redirectTo: "post", pathMatch: "full" },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BlogForumRoutingModule {}
