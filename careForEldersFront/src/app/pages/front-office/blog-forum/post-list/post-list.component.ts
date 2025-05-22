import { Component, OnInit } from "@angular/core"
import { Router, ActivatedRoute } from "@angular/router"
import { MatSnackBar } from "@angular/material/snack-bar"
import { MatDialog } from "@angular/material/dialog"
import { PageEvent } from "@angular/material/paginator"
import { PostService } from "../post.service"
import { Post } from "../models/post.model"
import { ConfirmDialogComponent } from "../confirm-dialog/confirm-dialog.component"

@Component({
  selector: "app-post-list",
  templateUrl: "./post-list.component.html",
  styleUrls: ["./post-list.component.scss"],
})
export class PostListComponent implements OnInit {
  posts: Post[] = []
  isLoading = false
  currentPage = 0
  pageSize = 10
  totalPosts = 0
  searchTerm = ""
  searchType = "title"
  currentUserId = "user123" // This should come from your auth service

  constructor(
    private postService: PostService,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.loadPosts()
  }

  loadPosts(): void {
    this.isLoading = true
    this.postService.getPosts(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.posts = response.content
        this.totalPosts = response.totalElements
        this.isLoading = false
      },
      error: (error) => {
        console.error("Error loading posts", error)
        this.snackBar.open("Failed to load posts", "Close", {
          duration: 3000,
        })
        this.isLoading = false
      },
    })
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex
    this.pageSize = event.pageSize
    this.loadPosts()
  }

  viewPost(postId: string): void {
    // Navigate to the post detail page
    this.router.navigate([postId], { relativeTo: this.route })
  }

  createPost(): void {
    // Navigate to the create post page
    this.router.navigate(["/user/userProfile/post/create"], { relativeTo: this.route })
  }

  editPost(postId: string, event: Event): void {
    event.stopPropagation()
    // Navigate to the edit post page
    this.router.navigate(["/user/userProfile/post/edit", postId], { relativeTo: this.route })
  }

  deletePost(postId: string, event: Event): void {
    event.stopPropagation()

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "350px",
      data: {
        title: "Delete Post",
        message: "Are you sure you want to delete this post? This action cannot be undone.",
      },
    })

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.isLoading = true
        this.postService.deletePost(postId).subscribe({
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
            this.isLoading = false
          },
        })
      }
    })
  }

  likePost(post: Post, event: Event): void {
    event.stopPropagation()

    // Check if user already liked the post
    const alreadyLiked = post.likes.some((like) => like.userId === this.currentUserId)

    if (alreadyLiked) {
      this.postService.unlikePost(post.id).subscribe({
        next: () => {
          this.refreshPost(post.id)
        },
        error: (error) => {
          console.error("Error unliking post", error)
          this.snackBar.open("Failed to unlike post", "Close", {
            duration: 3000,
          })
        },
      })
    } else {
      this.postService.likePost(post.id).subscribe({
        next: () => {
          this.refreshPost(post.id)
        },
        error: (error) => {
          console.error("Error liking post", error)
          this.snackBar.open("Failed to like post", "Close", {
            duration: 3000,
          })
        },
      })
    }
  }

  refreshPost(postId: string): void {
    this.postService.getPostById(postId).subscribe({
      next: (updatedPost) => {
        const index = this.posts.findIndex((p) => p.id === postId)
        if (index !== -1) {
          this.posts[index] = updatedPost
        }
      },
    })
  }

  isPostLikedByUser(post: Post): boolean {
    return post.likes.some((like) => like.userId === this.currentUserId)
  }

  isAuthor(authorId: string): boolean {
    return authorId === this.currentUserId
  }

  search(): void {
    if (!this.searchTerm.trim()) {
      this.loadPosts()
      return
    }

    this.isLoading = true

    let searchObservable

    switch (this.searchType) {
      case "title":
        searchObservable = this.postService.searchPostsByTitle(this.searchTerm)
        break
      case "content":
        searchObservable = this.postService.searchPostsByContent(this.searchTerm)
        break
      case "tag":
        searchObservable = this.postService.searchPostsByTag(this.searchTerm)
        break
      default:
        searchObservable = this.postService.searchPostsByTitle(this.searchTerm)
    }

    searchObservable.subscribe({
      next: (posts) => {
        this.posts = posts
        this.isLoading = false
      },
      error: (error) => {
        console.error("Error searching posts", error)
        this.snackBar.open("Failed to search posts", "Close", {
          duration: 3000,
        })
        this.isLoading = false
      },
    })
  }

  clearSearch(): void {
    this.searchTerm = ""
    this.loadPosts()
  }
}