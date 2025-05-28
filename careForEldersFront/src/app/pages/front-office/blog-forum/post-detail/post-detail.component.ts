import { Component,  OnInit, ViewChild,  OnDestroy } from "@angular/core"
import  { ActivatedRoute, Router } from "@angular/router"
import  { MatSnackBar } from "@angular/material/snack-bar"
import  { MatDialog } from "@angular/material/dialog"
import { Subscription } from "rxjs"
import  { PostService } from "../post.service"
import  { CommentService } from "../comment.service"
import  { SpeechToTextService, RecordingState } from "../speech-to-text.service"
import { Post } from "../models/post.model"
import  { Comment, CommentRequest } from "../models/comment.model"
import  { SpeechToTextComponent } from "../speech-to-text/speech-to-text.component"

@Component({
  selector: "app-post-detail",
  templateUrl: "./post-detail.component.html",
  styleUrls: ["./post-detail.component.scss"],
})
export class PostDetailComponent implements OnInit, OnDestroy {
  @ViewChild("speechToTextComponent") speechToTextComponent!: SpeechToTextComponent

  post: Post | null = null
  comments: Comment[] = []
  filteredComments: Comment[] = []
  isLoading = false
  isLoadingComments = false
  postId = ""
  currentUserId = "user123" // This should come from your auth service

  // Comment form
  newCommentContent = ""
  isSubmittingComment = false

  // Speech-to-text functionality
  showSpeechToText = false
  speechToTextLanguage = "en-US"
  isRecordingForComment = false
  isRecordingForReply = false
  recordingState: RecordingState = {
    isRecording: false,
    isProcessing: false,
    duration: 0,
    error: null,
    maxDurationReached: false,
  }

  // Reply functionality
  replyingToCommentId: string | null = null
  replyContent = ""
  isSubmittingReply = false

  // Enhanced Edit functionality
  editingCommentId: string | null = null
  editContent = ""
  originalEditContent = ""
  isUpdatingComment = false
  editingReplyId: string | null = null
  editReplyContent = ""
  originalEditReplyContent = ""
  isUpdatingReply = false

  // Post operations
  isDeletingPost = false
  isLikingPost = false

  // Filtering properties
  selectedSentimentFilter = "ALL"
  selectedEmotionFilter = "ALL"
  searchQuery = ""

  // Filter options
  sentimentOptions = [
    { value: "ALL", label: "All Sentiments" },
    { value: "POSITIVE", label: "Positive" },
    { value: "NEGATIVE", label: "Negative" },
    { value: "NEUTRAL", label: "Neutral" },
  ]

  emotionOptions = [
    { value: "ALL", label: "All Emotions" },
    { value: "JOY", label: "Joy" },
    { value: "ANGER", label: "Anger" },
    { value: "SADNESS", label: "Sadness" },
    { value: "FEAR", label: "Fear" },
    { value: "SURPRISE", label: "Surprise" },
    { value: "DISGUST", label: "Disgust" },
  ]

  // Sort options
  sortBy = "newest"
  sortOptions = [
    { value: "newest", label: "Newest First" },
    { value: "oldest", label: "Oldest First" },
    { value: "most_liked", label: "Most Liked" },
    { value: "sentiment_score", label: "Sentiment Score" },
  ]

  private subscription = new Subscription()

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService,
    private commentService: CommentService,
    private speechToTextService: SpeechToTextService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.postId = params["id"]
      if (this.postId) {
        this.loadPost()
        this.loadComments()
      }
    })

    // Subscribe to speech-to-text recording state
    this.subscription.add(
      this.speechToTextService.recordingState$.subscribe((state) => {
        this.recordingState = state
      }),
    )
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
    if (this.speechToTextComponent) {
      this.speechToTextComponent.cancelRecording()
    }
  }

  // Speech-to-text methods
  toggleSpeechToText(): void {
    this.showSpeechToText = !this.showSpeechToText
    if (!this.showSpeechToText && this.speechToTextComponent) {
      this.speechToTextComponent.cancelRecording()
      this.isRecordingForComment = false
      this.isRecordingForReply = false
    }
  }

  startRecordingForComment(): void {
    this.isRecordingForComment = true
    this.isRecordingForReply = false
    this.showSpeechToText = true

    // Focus on comment textarea
    setTimeout(() => {
      const textarea = document.getElementById("comment-textarea") as HTMLTextAreaElement
      if (textarea) {
        textarea.focus()
      }
    }, 100)
  }

  startRecordingForReply(): void {
    this.isRecordingForComment = false
    this.isRecordingForReply = true
    this.showSpeechToText = true

    // Focus on reply textarea
    setTimeout(() => {
      const textarea = document.getElementById("reply-textarea") as HTMLTextAreaElement
      if (textarea) {
        textarea.focus()
      }
    }, 100)
  }

  onTranscriptReceived(transcript: string): void {
    if (this.isRecordingForComment) {
      // Append to comment content
      if (this.newCommentContent.trim()) {
        this.newCommentContent += " " + transcript
      } else {
        this.newCommentContent = transcript
      }
      this.isRecordingForComment = false

      // Auto-resize textarea
      setTimeout(() => {
        const textarea = document.getElementById("comment-textarea") as HTMLTextAreaElement
        if (textarea) {
          this.autoResizeTextarea({ target: textarea })
        }
      }, 100)
    } else if (this.isRecordingForReply && this.replyingToCommentId) {
      // Append to reply content
      if (this.replyContent.trim()) {
        this.replyContent += " " + transcript
      } else {
        this.replyContent = transcript
      }
      this.isRecordingForReply = false

      // Auto-resize textarea
      setTimeout(() => {
        const textarea = document.getElementById("reply-textarea") as HTMLTextAreaElement
        if (textarea) {
          this.autoResizeTextarea({ target: textarea })
        }
      }, 100)
    }

    // Auto-hide speech-to-text panel after successful transcription
    setTimeout(() => {
      this.showSpeechToText = false
    }, 1000)
  }

  onRecordingStateChanged(state: RecordingState): void {
    this.recordingState = state

    // Show helpful messages
    if (state.isRecording) {
      const context = this.isRecordingForComment ? "comment" : "reply"
      this.snackBar.open(`Recording ${context}... Speak clearly`, "Close", {
        duration: 2000,
        panelClass: ["info-snackbar"],
      })
    }
  }

  onTranscriptionCompleted(event: { transcript: string; success: boolean }): void {
    if (event.success) {
      const context = this.isRecordingForComment ? "comment" : "reply"
      this.snackBar.open(`${context} transcribed successfully!`, "Close", {
        duration: 3000,
        panelClass: ["success-snackbar"],
      })
    }
  }

  insertTranscriptAtCursor(transcript: string, textareaId: string): void {
    const textarea = document.getElementById(textareaId) as HTMLTextAreaElement
    if (textarea) {
      const start = textarea.selectionStart
      const end = textarea.selectionEnd
      const currentValue = textarea.value

      const newValue = currentValue.substring(0, start) + transcript + currentValue.substring(end)

      // Update the model based on which textarea this is
      if (textareaId === "comment-textarea") {
        this.newCommentContent = newValue
      } else if (textareaId === "reply-textarea") {
        this.replyContent = newValue
      }

      // Set cursor position after inserted text
      const newCursorPosition = start + transcript.length
      setTimeout(() => {
        textarea.value = newValue
        textarea.setSelectionRange(newCursorPosition, newCursorPosition)
        textarea.focus()
        this.autoResizeTextarea({ target: textarea })
      }, 0)
    }
  }

  get isSpeechToTextSupported(): boolean {
    return this.speechToTextService.isRecordingSupported()
  }

  get speechToTextButtonTooltip(): string {
    if (!this.isSpeechToTextSupported) {
      return "Speech-to-text not supported in this browser"
    }
    if (this.recordingState.isRecording) {
      return "Recording in progress..."
    }
    if (this.recordingState.isProcessing) {
      return "Processing speech..."
    }
    return "Click to add text using voice"
  }

  // All existing methods from the original component...
  // (Include all the existing methods from your post-detail.component.ts)

  // Post CRUD Operations
  editPost(): void {
    if (!this.post) {
      this.snackBar.open("Post not found", "Close", { duration: 3000 })
      return
    }
    this.router.navigate(["/user/userProfile/post/edit", this.postId])
  }

  deletePost(): void {
    if (!this.post) {
      this.snackBar.open("Post not found", "Close", { duration: 3000 })
      return
    }

    const confirmDelete = confirm("Are you sure you want to delete this post? This action cannot be undone.")

    if (confirmDelete) {
      this.isDeletingPost = true

      this.postService.deletePost(this.postId).subscribe({
        next: () => {
          this.snackBar.open("Post deleted successfully", "Close", {
            duration: 3000,
            panelClass: ["success-snackbar"],
          })
          this.router.navigate(["/user/userProfile/post"])
        },
        error: (error) => {
          console.error("Error deleting post", error)
          this.snackBar.open("Failed to delete post. Please try again.", "Close", {
            duration: 5000,
            panelClass: ["error-snackbar"],
          })
          this.isDeletingPost = false
        },
      })
    }
  }

  likePost(): void {
    if (!this.post) {
      this.snackBar.open("Post not found", "Close", { duration: 3000 })
      return
    }

    this.isLikingPost = true

    this.postService.likePost(this.postId).subscribe({
      next: () => {
        this.loadPost()
        this.isLikingPost = false

        const message = "Post like status updated!"
        this.snackBar.open(message, "Close", {
          duration: 2000,
          panelClass: ["success-snackbar"],
        })
      },
      error: (error) => {
        console.error("Error liking post", error)
        this.snackBar.open("Failed to like post. Please try again.", "Close", {
          duration: 3000,
          panelClass: ["error-snackbar"],
        })
        this.isLikingPost = false
      },
    })
  }

  isPostLikedByUser(): boolean {
    if (!this.post || !this.post.likes) return false
    return this.post.likes.some((like) => like.userId === this.currentUserId)
  }

  // Enhanced Edit Comment Methods
  startEditComment(comment: Comment): void {
    this.cancelAllEdits()
    this.editingCommentId = comment.id
    this.editContent = comment.content || ""
    this.originalEditContent = comment.content || ""

    setTimeout(() => {
      const textarea = document.getElementById(`edit-comment-${comment.id}`) as HTMLTextAreaElement
      if (textarea) {
        textarea.focus()
        textarea.setSelectionRange(textarea.value.length, textarea.value.length)
      }
    }, 100)
  }

  startEditReply(reply: Comment): void {
    this.cancelAllEdits()
    this.editingReplyId = reply.id
    this.editReplyContent = reply.content || ""
    this.originalEditReplyContent = reply.content || ""

    setTimeout(() => {
      const textarea = document.getElementById(`edit-reply-${reply.id}`) as HTMLTextAreaElement
      if (textarea) {
        textarea.focus()
        textarea.setSelectionRange(textarea.value.length, textarea.value.length)
      }
    }, 100)
  }

  cancelEditComment(): void {
    this.editingCommentId = null
    this.editContent = ""
    this.originalEditContent = ""
  }

  cancelEditReply(): void {
    this.editingReplyId = null
    this.editReplyContent = ""
    this.originalEditReplyContent = ""
  }

  cancelAllEdits(): void {
    this.cancelEditComment()
    this.cancelEditReply()
    this.cancelReply()
    this.showSpeechToText = false
    this.isRecordingForComment = false
    this.isRecordingForReply = false
  }

  submitEditComment(): void {
    if (!this.editContent.trim() || !this.editingCommentId) {
      this.snackBar.open("Comment content cannot be empty", "Close", { duration: 3000 })
      return
    }

    if (this.editContent.trim() === this.originalEditContent.trim()) {
      this.cancelEditComment()
      return
    }

    this.isUpdatingComment = true
    const updateRequest: CommentRequest = {
      content: this.editContent.trim(),
      postId: this.postId,
      userId: this.currentUserId,
    }

    this.commentService.updateComment(this.editingCommentId, updateRequest).subscribe({
      next: (updatedComment) => {
        const comment = this.findCommentById(this.editingCommentId!)
        if (comment) {
          Object.assign(comment, {
            ...updatedComment,
            updatedAt: new Date().toISOString(),
            editCount: (comment.editCount || 0) + 1,
            lastEditedAt: new Date().toISOString(),
          })

          comment.wordCount = this.getWordCount(comment.content || "")
          comment.characterCount = this.getCharacterCount(comment.content || "")
          comment.readingTimeSeconds = this.getReadingTimeSeconds(comment.content || "")
          comment.keywords = this.getKeywordChips(comment.content || "")
        }

        this.applyFilters()
        this.cancelEditComment()
        this.isUpdatingComment = false
        this.snackBar.open("Comment updated successfully", "Close", {
          duration: 3000,
          panelClass: ["success-snackbar"],
        })
      },
      error: (error) => {
        console.error("Error updating comment", error)
        this.snackBar.open("Failed to update comment. Please try again.", "Close", {
          duration: 5000,
          panelClass: ["error-snackbar"],
        })
        this.isUpdatingComment = false
      },
    })
  }

  submitEditReply(): void {
    if (!this.editReplyContent.trim() || !this.editingReplyId) {
      this.snackBar.open("Reply content cannot be empty", "Close", { duration: 3000 })
      return
    }

    if (this.editReplyContent.trim() === this.originalEditReplyContent.trim()) {
      this.cancelEditReply()
      return
    }

    this.isUpdatingReply = true
    const updateRequest: CommentRequest = {
      content: this.editReplyContent.trim(),
      postId: this.postId,
      userId: this.currentUserId,
    }

    this.commentService.updateComment(this.editingReplyId, updateRequest).subscribe({
      next: (updatedReply) => {
        const reply = this.findCommentById(this.editingReplyId!)
        if (reply) {
          Object.assign(reply, {
            ...updatedReply,
            updatedAt: new Date().toISOString(),
            editCount: (reply.editCount || 0) + 1,
            lastEditedAt: new Date().toISOString(),
          })

          reply.wordCount = this.getWordCount(reply.content || "")
          reply.characterCount = this.getCharacterCount(reply.content || "")
          reply.readingTimeSeconds = this.getReadingTimeSeconds(reply.content || "")
          reply.keywords = this.getKeywordChips(reply.content || "")
        }

        this.cancelEditReply()
        this.isUpdatingReply = false
        this.snackBar.open("Reply updated successfully", "Close", {
          duration: 3000,
          panelClass: ["success-snackbar"],
        })
      },
      error: (error) => {
        console.error("Error updating reply", error)
        this.snackBar.open("Failed to update reply. Please try again.", "Close", {
          duration: 5000,
          panelClass: ["error-snackbar"],
        })
        this.isUpdatingReply = false
      },
    })
  }

  // Validation and helper methods
  canEditComment(comment: Comment): boolean {
    if (!comment || !comment.authorId) return false
    return comment.authorId === this.currentUserId
  }

  canEditPost(): boolean {
    if (!this.post || !this.post.authorId) return false
    return this.post.authorId === this.currentUserId
  }

  wasCommentEdited(comment: Comment): boolean {
    if (!comment || !comment.updatedAt || !comment.createdAt) return false
    return new Date(comment.updatedAt).getTime() > new Date(comment.createdAt).getTime()
  }

  getEditInfoText(comment: Comment): string {
    if (!this.wasCommentEdited(comment)) return ""

    const editCount = comment.editCount || 1
    const lastEdited = comment.lastEditedAt || comment.updatedAt
    if (!lastEdited) return ""

    const timeAgo = this.getTimeAgo(new Date(lastEdited))

    if (editCount === 1) {
      return `Edited ${timeAgo}`
    } else {
      return `Edited ${editCount} times, last ${timeAgo}`
    }
  }

  autoResizeTextarea(event: any): void {
    const textarea = event.target
    textarea.style.height = "auto"
    textarea.style.height = textarea.scrollHeight + "px"
  }

  onEditKeydown(event: KeyboardEvent, type: "comment" | "reply"): void {
    if ((event.ctrlKey || event.metaKey) && event.key === "Enter") {
      event.preventDefault()
      if (type === "comment") {
        this.submitEditComment()
      } else {
        this.submitEditReply()
      }
    }

    if (event.key === "Escape") {
      event.preventDefault()
      if (type === "comment") {
        this.cancelEditComment()
      } else {
        this.cancelEditReply()
      }
    }
  }

  isEditContentValid(content: string): boolean {
    if (!content) return false
    return content.trim().length >= 1 && content.trim().length <= 5000
  }

  getEditContentError(content: string): string {
    if (!content || !content.trim()) {
      return "Content cannot be empty"
    }
    if (content.trim().length > 5000) {
      return "Content cannot exceed 5000 characters"
    }
    return ""
  }

  getTimeAgo(date: Date): string {
    const now = new Date()
    const diffInMs = now.getTime() - date.getTime()
    const diffInMinutes = Math.floor(diffInMs / (1000 * 60))
    const diffInHours = Math.floor(diffInMinutes / 60)
    const diffInDays = Math.floor(diffInHours / 24)

    if (diffInMinutes < 1) {
      return "just now"
    } else if (diffInMinutes < 60) {
      return `${diffInMinutes}m ago`
    } else if (diffInHours < 24) {
      return `${diffInHours}h ago`
    } else if (diffInDays < 7) {
      return `${diffInDays}d ago`
    } else {
      return date.toLocaleDateString()
    }
  }

  // Filtering methods
  applyFilters(): void {
    this.filteredComments = this.comments.filter((comment) => {
      if (this.selectedSentimentFilter !== "ALL") {
        if (!comment.sentiment || comment.sentiment.toUpperCase() !== this.selectedSentimentFilter) {
          return false
        }
      }

      if (this.selectedEmotionFilter !== "ALL") {
        if (
          !comment.emotions ||
          !comment.emotions.some(
            (emotion) => emotion.emotion && emotion.emotion.toUpperCase() === this.selectedEmotionFilter,
          )
        ) {
          return false
        }
      }

      if (this.searchQuery.trim()) {
        const query = this.searchQuery.toLowerCase()
        const content = comment.content || ""
        const authorName = comment.authorName || comment.authorId || ""
        if (!content.toLowerCase().includes(query) && !authorName.toLowerCase().includes(query)) {
          return false
        }
      }

      return true
    })

    this.applySorting()
  }

  applySorting(): void {
    switch (this.sortBy) {
      case "newest":
        this.filteredComments.sort(
          (a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime(),
        )
        break
      case "oldest":
        this.filteredComments.sort(
          (a, b) => new Date(a.createdAt || 0).getTime() - new Date(b.createdAt || 0).getTime(),
        )
        break
      case "most_liked":
        this.filteredComments.sort((a, b) => (b.likes?.length || 0) - (a.likes?.length || 0))
        break
      case "sentiment_score":
        this.filteredComments.sort((a, b) => (b.sentimentScore || 0) - (a.sentimentScore || 0))
        break
    }
  }

  onSentimentFilterChange(): void {
    this.applyFilters()
  }

  onEmotionFilterChange(): void {
    this.applyFilters()
  }

  onSearchQueryChange(): void {
    this.applyFilters()
  }

  onSortChange(): void {
    this.applySorting()
  }

  clearFilters(): void {
    this.selectedSentimentFilter = "ALL"
    this.selectedEmotionFilter = "ALL"
    this.searchQuery = ""
    this.sortBy = "newest"
    this.applyFilters()
  }

  // Statistics methods
  getSentimentStats(): { [key: string]: number } {
    const stats: { [key: string]: number } = {
      positive: 0,
      negative: 0,
      neutral: 0,
      total: this.comments.length,
    }

    this.comments.forEach((comment) => {
      if (comment.sentiment) {
        switch (comment.sentiment.toLowerCase()) {
          case "positive":
            stats["positive"]++
            break
          case "negative":
            stats["negative"]++
            break
          case "neutral":
            stats["neutral"]++
            break
        }
      }
    })

    return stats
  }

  getEmotionStats(): Array<{ emotion: string; count: number }> {
    const emotionCounts: { [key: string]: number } = {}

    this.comments.forEach((comment) => {
      if (comment.emotions) {
        comment.emotions.forEach((emotion) => {
          if (emotion.emotion) {
            const emotionName = emotion.emotion.toLowerCase()
            emotionCounts[emotionName] = (emotionCounts[emotionName] || 0) + 1
          }
        })
      }
    })

    return Object.entries(emotionCounts)
      .map(([emotion, count]) => ({ emotion, count }))
      .sort((a, b) => b.count - a.count)
  }

  // Helper methods for template
  getWordCount(text: string): number {
    if (!text) return 0
    return text
      .trim()
      .split(/\s+/)
      .filter((word) => word.length > 0).length
  }

  getCharacterCount(text: string): number {
    return text ? text.length : 0
  }

  getReadingTimeSeconds(text: string): number {
    const wordCount = this.getWordCount(text)
    const wordsPerMinute = 200
    const minutes = wordCount / wordsPerMinute
    return Math.ceil(minutes * 60)
  }

  formatReadingTime(seconds: number): string {
    if (seconds < 60) {
      return `${seconds}s read`
    } else {
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      if (remainingSeconds === 0) {
        return `${minutes}m read`
      } else {
        return `${minutes}m ${remainingSeconds}s read`
      }
    }
  }

  getKeywordChips(text: string | string[]): string[] {
    if (!text) return []

    if (Array.isArray(text)) {
      return text.slice(0, 5)
    }

    const words = text
      .toLowerCase()
      .replace(/[^\w\s]/g, "")
      .split(/\s+/)
      .filter((word) => word.length > 3)
      .filter((word) => !this.isStopWord(word))

    const uniqueWords = [...new Set(words)]
    return uniqueWords.slice(0, 5)
  }

  private isStopWord(word: string): boolean {
    const stopWords = [
      "this",
      "that",
      "with",
      "have",
      "will",
      "from",
      "they",
      "know",
      "want",
      "been",
      "good",
      "much",
      "some",
      "time",
      "very",
      "when",
      "come",
      "here",
      "just",
      "like",
      "long",
      "make",
      "many",
      "over",
      "such",
      "take",
      "than",
      "them",
      "well",
      "were",
    ]
    return stopWords.includes(word.toLowerCase())
  }

  getSentimentClass(sentiment: string): string {
    if (!sentiment) return "sentiment-unknown"
    switch (sentiment.toLowerCase()) {
      case "positive":
        return "sentiment-positive"
      case "negative":
        return "sentiment-negative"
      case "neutral":
        return "sentiment-neutral"
      default:
        return "sentiment-unknown"
    }
  }

  getSentimentIcon(sentiment: string): string {
    if (!sentiment) return "help_outline"
    switch (sentiment.toLowerCase()) {
      case "positive":
        return "sentiment_satisfied"
      case "negative":
        return "sentiment_dissatisfied"
      case "neutral":
        return "sentiment_neutral"
      default:
        return "help_outline"
    }
  }

  getEmotionIcon(emotion: string): string {
    if (!emotion) return "help_outline"
    switch (emotion.toLowerCase()) {
      case "joy":
        return "mood"
      case "anger":
        return "mood_bad"
      case "sadness":
        return "sentiment_very_dissatisfied"
      case "fear":
        return "warning"
      case "surprise":
        return "help_outline"
      case "disgust":
        return "thumb_down"
      default:
        return "help_outline"
    }
  }

  // CRUD operations
  loadPost(): void {
    this.isLoading = true
    this.postService.getPostById(this.postId).subscribe({
      next: (post) => {
        this.post = post
        this.isLoading = false
      },
      error: (error) => {
        console.error("Error loading post", error)
        this.snackBar.open("Failed to load post", "Close", { duration: 3000 })
        this.isLoading = false
        this.router.navigate(["/user/userProfile/post"])
      },
    })
  }

  loadComments(): void {
    this.isLoadingComments = true
    this.commentService.getCommentsByPostId(this.postId).subscribe({
      next: (comments) => {
        this.comments = comments.map((comment) => ({
          ...comment,
          wordCount: comment.wordCount || this.getWordCount(comment.content || ""),
          characterCount: comment.characterCount || this.getCharacterCount(comment.content || ""),
          readingTimeSeconds: comment.readingTimeSeconds || this.getReadingTimeSeconds(comment.content || ""),
          keywords: comment.keywords || this.getKeywordChips(comment.content || ""),
        }))

        this.applyFilters()
        this.isLoadingComments = false
      },
      error: (error) => {
        console.error("Error loading comments", error)
        this.snackBar.open("Failed to load comments", "Close", { duration: 3000 })
        this.isLoadingComments = false
      },
    })
  }

  addComment(): void {
    if (!this.newCommentContent.trim()) {
      return
    }

    this.isSubmittingComment = true
    const commentRequest: CommentRequest = {
      content: this.newCommentContent,
      postId: this.postId,
      userId: this.currentUserId,
    }

    this.commentService.addComment(commentRequest).subscribe({
      next: (comment) => {
        this.comments.unshift(comment)
        this.applyFilters()
        this.newCommentContent = ""
        this.isSubmittingComment = false
        this.snackBar.open("Comment added successfully", "Close", { duration: 3000 })
      },
      error: (error) => {
        console.error("Error adding comment", error)
        this.snackBar.open("Failed to add comment", "Close", { duration: 3000 })
        this.isSubmittingComment = false
      },
    })
  }

  startReply(commentId: string): void {
    this.cancelAllEdits()
    this.replyingToCommentId = commentId
    this.replyContent = ""
  }

  cancelReply(): void {
    this.replyingToCommentId = null
    this.replyContent = ""
    this.isRecordingForReply = false
  }

  submitReply(): void {
    if (!this.replyContent.trim() || !this.replyingToCommentId) {
      return
    }

    this.isSubmittingReply = true
    const replyRequest: CommentRequest = {
      content: this.replyContent,
      postId: this.postId,
      userId: this.currentUserId,
      parentCommentId: this.replyingToCommentId,
    }

    this.commentService.addReply(this.replyingToCommentId, replyRequest).subscribe({
      next: (reply) => {
        const parentComment = this.findCommentById(this.replyingToCommentId!)
        if (parentComment) {
          if (!parentComment.replies) {
            parentComment.replies = []
          }
          parentComment.replies.push(reply)
        }

        this.cancelReply()
        this.isSubmittingReply = false
        this.snackBar.open("Reply added successfully", "Close", { duration: 3000 })
      },
      error: (error) => {
        console.error("Error adding reply", error)
        this.snackBar.open("Failed to add reply", "Close", { duration: 3000 })
        this.isSubmittingReply = false
      },
    })
  }

  deleteComment(commentId: string): void {
    if (confirm("Are you sure you want to delete this comment?")) {
      this.commentService.deleteComment(commentId).subscribe({
        next: () => {
          this.comments = this.comments.filter((c) => c.id !== commentId)
          this.applyFilters()
          this.snackBar.open("Comment deleted successfully", "Close", { duration: 3000 })
        },
        error: (error) => {
          console.error("Error deleting comment", error)
          this.snackBar.open("Failed to delete comment", "Close", { duration: 3000 })
        },
      })
    }
  }

  likeComment(comment: Comment): void {
    this.commentService.likeComment(comment.id, this.currentUserId).subscribe({
      next: (updatedComment) => {
        Object.assign(comment, updatedComment)
      },
      error: (error) => {
        console.error("Error liking comment", error)
        this.snackBar.open("Failed to like comment", "Close", { duration: 3000 })
      },
    })
  }

  isCommentLikedByUser(comment: Comment): boolean {
    return comment.likes?.some((like) => like.userId === this.currentUserId) || false
  }

  isCommentAuthor(comment: Comment | Post): boolean {
    return comment.authorId === this.currentUserId
  }

  private findCommentById(commentId: string): Comment | null {
    for (const comment of this.comments) {
      if (comment.id === commentId) {
        return comment
      }
      if (comment.replies) {
        for (const reply of comment.replies) {
          if (reply.id === commentId) {
            return reply
          }
        }
      }
    }
    return null
  }

  openImageModal(image: any, index: number): void {
    window.open(image.url, "_blank")
  }

  goBack(): void {
    this.router.navigate(["/user/userProfile/post"])
  }
}
