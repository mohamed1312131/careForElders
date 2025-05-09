import { Component,  OnInit } from "@angular/core"
import  { ActivatedRoute, Router } from "@angular/router"
import  { MatDialog } from "@angular/material/dialog"
import  { MatSnackBar } from "@angular/material/snack-bar"
//import type { PostService } from "../../../../services/post.service"
//import { ConfirmDialogComponent } from "./confirm-dialog.component"
import { PostService } from "../post.service"
import { ConfirmDialogComponent } from "../../user-service/user/confirm-dialog/confirm-dialog.component"

@Component({
  selector: "app-post-detail",
  templateUrl: "./post-detail.component.html",
  styleUrls: ["./post-detail.component.scss"],
})
export class PostDetailComponent implements OnInit {
  post: any = null
  isLoading = true
  error: string | null = null

  // Mock user ID for demo purposes
  currentUserId = "user123"

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadPost()
  }

  loadPost(): void {
    const id = this.route.snapshot.paramMap.get("id")
    if (!id) {
      this.error = "Post ID is missing"
      this.isLoading = false
      return
    }

    this.postService.getPostById(id).subscribe({
      next: (post) => {
        this.post = post
        this.isLoading = false
      },
      error: (error) => {
        console.error("Error loading post", error)
        this.error = "Failed to load post. It may have been deleted or you don't have permission to view it."
        this.isLoading = false
      },
    })
  }

  isAuthor(): boolean {
    return this.post?.authorId === this.currentUserId
  }

  editPost(): void {
    if (this.post) {
      this.router.navigate(["/blog-forum/post/edit", this.post.id])
    }
  }

  deletePost(): void {
    if (!this.post) return

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "350px",
      data: {
        title: "Delete Post",
        message: "Are you sure you want to delete this post? This action cannot be undone.",
      },
    })

    dialogRef.afterClosed().subscribe((result) => {
      if (result && this.post?.id) {
        this.postService.deletePost(this.post.id).subscribe({
          next: () => {
            this.snackBar.open("Post deleted successfully", "Close", {
              duration: 3000,
            })
            this.router.navigate(["/blog-forum/post"])
          },
          error: (error) => {
            console.error("Error deleting post", error)
            this.snackBar.open("Failed to delete post", "Close", {
              duration: 3000,
            })
          },
        })
      }
    })
  }
}
