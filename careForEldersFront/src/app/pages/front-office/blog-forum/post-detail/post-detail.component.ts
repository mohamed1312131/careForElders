import { Component, OnInit } from "@angular/core"
import { ActivatedRoute, Router } from "@angular/router"
import { MatSnackBar } from "@angular/material/snack-bar"
import { MatDialog } from "@angular/material/dialog"
import { FormBuilder, FormGroup, Validators } from "@angular/forms"
import { PostService } from "../post.service"
import { CommentService } from "../comment.service"
import { Post } from "../models/post.model"
import { Comment } from "../models/comment.model"
import { ConfirmDialogComponent } from "../confirm-dialog/confirm-dialog.component"

@Component({
  selector: "app-post-detail",
  templateUrl: "./post-detail.component.html",
  styleUrls: ["./post-detail.component.scss"],
})
export class PostDetailComponent implements OnInit {
  post: Post | null = null
  comments: Comment[] = []
  isLoading = false
  isLoadingComments = false
  commentForm: FormGroup
  replyForm: FormGroup
  editCommentForm: FormGroup
  replyingTo: string | null = null
  editingCommentId: string | null = null
  currentUserId = "user123" // This should come from your auth service

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService,
    private commentService: CommentService,
    private snackBar: MatSnackBar,
    private fb: FormBuilder,
    private dialog: MatDialog,
  ) {
    this.commentForm = this.fb.group({
      content: ["", [Validators.required, Validators.minLength(3)]],
    })

    this.replyForm = this.fb.group({
      content: ["", [Validators.required, Validators.minLength(3)]],
    })

    this.editCommentForm = this.fb.group({
      content: ["", [Validators.required, Validators.minLength(3)]],
    })
  }

  ngOnInit(): void {
    const postId = this.route.snapshot.paramMap.get("id")
    if (postId) {
      this.loadPost(postId)
      this.loadComments(postId)
    } else {
      this.router.navigate(["../"], { relativeTo: this.route })
    }
  }

  loadPost(id: string): void {
    this.isLoading = true
    this.postService.getPostById(id).subscribe({
      next: (post) => {
        this.post = post
        this.isLoading = false
      },
      error: (error) => {
        console.error("Error loading post", error)
        this.snackBar.open("Failed to load post", "Close", {
          duration: 3000,
        })
        this.isLoading = false
        this.router.navigate(["../"], { relativeTo: this.route })
      },
    })
  }

  loadComments(postId: string): void {
    this.isLoadingComments = true
    this.commentService.getCommentsByPostId(postId).subscribe({
      next: (comments) => {
        this.comments = comments
        this.isLoadingComments = false
      },
      error: (error) => {
        console.error("Error loading comments", error)
        this.snackBar.open("Failed to load comments", "Close", {
          duration: 3000,
        })
        this.isLoadingComments = false
      },
    })
  }

  submitComment(): void {
    if (this.commentForm.invalid || !this.post) {
      return
    }

    const commentRequest = {
      content: this.commentForm.value.content,
    }

    this.commentService.createComment(this.post.id, commentRequest).subscribe({
      next: () => {
        this.snackBar.open("Comment added successfully", "Close", {
          duration: 3000,
        })
        this.commentForm.reset()
        this.loadComments(this.post!.id)
      },
      error: (error) => {
        console.error("Error adding comment", error)
        this.snackBar.open("Failed to add comment", "Close", {
          duration: 3000,
        })
      },
    })
  }

  startReply(commentId: string): void {
    this.replyingTo = commentId
    this.editingCommentId = null
    this.replyForm.reset()
  }

  cancelReply(): void {
    this.replyingTo = null
  }

  submitReply(commentId: string): void {
    if (this.replyForm.invalid || !this.post) {
      return
    }

    const replyRequest = {
      content: this.replyForm.value.content,
      parentCommentId: commentId,
    }

    this.commentService.createComment(this.post.id, replyRequest).subscribe({
      next: () => {
        this.snackBar.open("Reply added successfully", "Close", {
          duration: 3000,
        })
        this.replyForm.reset()
        this.replyingTo = null
        this.loadComments(this.post!.id)
      },
      error: (error) => {
        console.error("Error adding reply", error)
        this.snackBar.open("Failed to add reply", "Close", {
          duration: 3000,
        })
      },
    })
  }

  editComment(commentId: string): void {
    this.editingCommentId = commentId
    this.replyingTo = null

    // Find the comment to edit
    let commentToEdit: Comment | undefined

    for (const comment of this.comments) {
      if (comment.id === commentId) {
        commentToEdit = comment
        break
      }

      if (comment.replies) {
        const reply = comment.replies.find((r) => r.id === commentId)
        if (reply) {
          commentToEdit = reply
          break
        }
      }
    }

    if (commentToEdit) {
      this.editCommentForm.patchValue({
        content: commentToEdit.content,
      })
    }
  }

  cancelEditComment(): void {
    this.editingCommentId = null
  }

  submitEditComment(): void {
    if (this.editCommentForm.invalid || !this.post || !this.editingCommentId) {
      return
    }

    const commentRequest = {
      content: this.editCommentForm.value.content,
    }

    this.commentService.updateComment(this.post.id, this.editingCommentId, commentRequest).subscribe({
      next: () => {
        this.snackBar.open("Comment updated successfully", "Close", {
          duration: 3000,
        })
        this.editCommentForm.reset()
        this.editingCommentId = null
        this.loadComments(this.post!.id)
      },
      error: (error) => {
        console.error("Error updating comment", error)
        this.snackBar.open("Failed to update comment", "Close", {
          duration: 3000,
        })
      },
    })
  }

  likePost(): void {
    if (!this.post) return

    this.postService.likePost(this.post.id).subscribe({
      next: () => {
        this.loadPost(this.post!.id)
      },
      error: (error) => {
        console.error("Error liking post", error)
        this.snackBar.open("Failed to like post", "Close", {
          duration: 3000,
        })
      },
    })
  }

  unlikePost(): void {
    if (!this.post) return

    this.postService.unlikePost(this.post.id).subscribe({
      next: () => {
        this.loadPost(this.post!.id)
      },
      error: (error) => {
        console.error("Error unliking post", error)
        this.snackBar.open("Failed to unlike post", "Close", {
          duration: 3000,
        })
      },
    })
  }

  likeComment(commentId: string): void {
    if (!this.post) return

    this.commentService.likeComment(this.post.id, commentId).subscribe({
      next: () => {
        this.loadComments(this.post!.id)
      },
      error: (error) => {
        console.error("Error liking comment", error)
        this.snackBar.open("Failed to like comment", "Close", {
          duration: 3000,
        })
      },
    })
  }

  unlikeComment(commentId: string): void {
    if (!this.post) return

    this.commentService.unlikeComment(this.post.id, commentId).subscribe({
      next: () => {
        this.loadComments(this.post!.id)
      },
      error: (error) => {
        console.error("Error unliking comment", error)
        this.snackBar.open("Failed to unlike comment", "Close", {
          duration: 3000,
        })
      },
    })
  }

  editPost(): void {
    if (!this.post) return
    this.router.navigate(["/user/userProfile/post/edit", this.post.id], { relativeTo: this.route })
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
      if (result) {
        this.postService.deletePost(this.post!.id).subscribe({
          next: () => {
            this.snackBar.open("Post deleted successfully", "Close", {
              duration: 3000,
            })
            this.router.navigate(["/user/userProfile/blog"], { relativeTo: this.route })
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

  deleteComment(commentId: string): void {
    if (!this.post) return

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "350px",
      data: {
        title: "Delete Comment",
        message: "Are you sure you want to delete this comment? This action cannot be undone.",
      },
    })

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.commentService.deleteComment(this.post!.id, commentId).subscribe({
          next: () => {
            this.snackBar.open("Comment deleted successfully", "Close", {
              duration: 3000,
            })
            this.loadComments(this.post!.id)
          },
          error: (error) => {
            console.error("Error deleting comment", error)
            this.snackBar.open("Failed to delete comment", "Close", {
              duration: 3000,
            })
          },
        })
      }
    })
  }

  isCommentLikedByUser(comment: Comment): boolean {
    return comment.likes.some((like) => like.userId === this.currentUserId)
  }

  isPostLikedByUser(): boolean {
    if (!this.post) return false
    return this.post.likes.some((like) => like.userId === this.currentUserId)
  }

  isAuthor(authorId: string): boolean {
    return authorId === this.currentUserId
  }

  goBack(): void {
    this.router.navigate(["/user/userProfile/blog"], { relativeTo: this.route })
  }
}