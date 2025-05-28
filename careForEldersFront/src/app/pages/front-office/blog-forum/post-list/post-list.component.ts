import { Component, OnInit } from "@angular/core"
import { Router, ActivatedRoute } from "@angular/router"
import { MatSnackBar } from "@angular/material/snack-bar"
import { MatDialog } from "@angular/material/dialog"
import { PageEvent } from "@angular/material/paginator"
import { PostService } from "../post.service"
import { CommentService } from "../comment.service"
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
  
  // Expose Math object to template
  Math = Math
  
  // Filter and sort options
  sortBy = "createdAt"
  sortDirection = "desc"
  filterOptions = [
    { value: "all", label: "All Posts" },
    { value: "recent", label: "Recent Posts" },
    { value: "popular", label: "Most Liked" },
    { value: "discussed", label: "Most Discussed" }
  ]
  selectedFilter = "all"

  constructor(
    private postService: PostService,
    private commentService: CommentService,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.loadPosts()
  }

  // Helper method for pagination display
  getPaginationText(): string {
    const start = this.currentPage * this.pageSize + 1
    const end = Math.min((this.currentPage + 1) * this.pageSize, this.totalPosts)
    return `Showing ${start} - ${end} of ${this.totalPosts} posts`
  }

  // Alternative helper methods for template
  getStartIndex(): number {
    return this.currentPage * this.pageSize + 1
  }

  getEndIndex(): number {
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalPosts)
  }

  loadPosts(): void {
    this.isLoading = true
    this.postService.getPosts(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.posts = response.content
        this.totalPosts = response.totalElements
        
        // Load accurate comment counts for each post
        this.loadCommentCounts()
        
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

  loadCommentCounts(): void {
    // Load comment counts for all posts
    this.posts.forEach(post => {
      this.commentService.getCommentsByPostId(post.id).subscribe({
        next: (comments) => {
          post.commentsCount = comments.length
        },
        error: (error) => {
          console.error(`Error loading comments for post ${post.id}`, error)
          post.commentsCount = 0
        }
      })
    })
  }

  onFilterChange(): void {
    switch (this.selectedFilter) {
      case "recent":
        this.sortBy = "createdAt"
        this.sortDirection = "desc"
        break
      case "popular":
        this.sortBy = "likes"
        this.sortDirection = "desc"
        break
      case "discussed":
        this.sortBy = "commentsCount"
        this.sortDirection = "desc"
        break
      default:
        this.sortBy = "createdAt"
        this.sortDirection = "desc"
    }
    this.currentPage = 0
    this.loadPosts()
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex
    this.pageSize = event.pageSize
    this.loadPosts()
  }

  viewPost(postId: string): void {
    this.router.navigate(["/user/userProfile/post/", postId], { relativeTo: this.route })
  }

  createPost(): void {
    this.router.navigate(["/user/userProfile/post/create"], { relativeTo: this.route })
  }

  editPost(postId: string, event: Event): void {
    event.stopPropagation()
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
        this.loadCommentCounts() // Load comment counts for search results
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

  getTimeAgo(date: Date): string {
    const now = new Date()
    const postDate = new Date(date)
    const diffInMs = now.getTime() - postDate.getTime()
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60))
    const diffInDays = Math.floor(diffInHours / 24)

    if (diffInHours < 1) {
      return "Just now"
    } else if (diffInHours < 24) {
      return `${diffInHours}h ago`
    } else if (diffInDays < 7) {
      return `${diffInDays}d ago`
    } else {
      return postDate.toLocaleDateString()
    }
  }

  getPostEngagement(post: Post): number {
    return (post.likes?.length || 0) + (post.commentsCount || 0) + (post.viewCount || 0)
  }
}