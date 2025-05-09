import { Component, type OnInit } from "@angular/core"
import type { FormBuilder, FormGroup } from "@angular/forms"
import type { PageEvent } from "@angular/material/paginator"
import type { MatDialog } from "@angular/material/dialog"
import type { MatSnackBar } from "@angular/material/snack-bar"
import { PostService } from "../post.service"
import { ConfirmDialogComponent } from "../confirm-dialog/confirm-dialog.component"
//import { ConfirmDialogComponent } from "./confirm-dialog/confirm-dialog.component"

@Component({
  selector: "app-post-list",
  templateUrl: "./post-list.component.html",
  styleUrls: ["./post-list.component.scss"],
})
export class PostListComponent implements OnInit {
  posts: any[] = []
  totalPosts = 0
  pageSize = 10
  currentPage = 0
  pageSizeOptions: number[] = [5, 10, 25, 50]
  sortOptions = [
    { value: "createdAt", viewValue: "Date Created" },
    { value: "title", viewValue: "Title" },
    { value: "viewCount", viewValue: "View Count" },
  ]
  sortDirections = [
    { value: "desc", viewValue: "Descending" },
    { value: "asc", viewValue: "Ascending" },
  ]
  searchForm: FormGroup
  isLoading = false

  // Mock user ID for demo purposes
  currentUserId = "user123"

  constructor(
    private postService: PostService,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {
    this.searchForm = this.fb.group({
      searchType: ["title"],
      searchTerm: [""],
      sortBy: ["createdAt"],
      direction: ["desc"],
    })
  }

  ngOnInit(): void {
    this.loadPosts()
  }

  loadPosts(): void {
    this.isLoading = true
    const { sortBy, direction } = this.searchForm.value

    this.postService.getAllPosts(this.currentPage, this.pageSize, sortBy, direction).subscribe({
      next: (response: any) => {
        this.posts = response.content
        this.totalPosts = response.totalElements
        this.isLoading = false
      },
      error: (error) => {
        console.error("Error loading posts", error)
        this.isLoading = false
      },
    })
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex
    this.pageSize = event.pageSize
    this.loadPosts()
  }

  onSortChange(): void {
    this.currentPage = 0
    this.loadPosts()
  }

  onSearch(): void {
    this.isLoading = true
    const { searchType, searchTerm } = this.searchForm.value

    if (!searchTerm.trim()) {
      this.loadPosts()
      return
    }

    let title, content, tag

    switch (searchType) {
      case "title":
        title = searchTerm
        break
      case "content":
        content = searchTerm
        break
      case "tag":
        tag = searchTerm
        break
    }

    this.postService.searchPosts(title, content, tag).subscribe({
      next: (posts: any[]) => {
        this.posts = posts
        this.totalPosts = posts.length
        this.isLoading = false
      },
      error: (error) => {
        console.error("Error searching posts", error)
        this.isLoading = false
      },
    })
  }

  clearSearch(): void {
    this.searchForm.get("searchTerm")?.setValue("")
    this.loadPosts()
  }

  isAuthor(post: any): boolean {
    return post.authorId === this.currentUserId
  }

  deletePost(id?: string): void {
    if (!id) return

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "350px",
      data: {
        title: "Delete Post",
        message: "Are you sure you want to delete this post? This action cannot be undone.",
      },
    })

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.postService.deletePost(id).subscribe({
          next: () => {
            this.snackBar.open("Post deleted successfully", "Close", {
              duration: 3000,
            })
            this.loadPosts()
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
