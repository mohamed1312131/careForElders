import { Component, type OnInit } from "@angular/core"
import { type FormBuilder, FormGroup, Validators, type FormArray } from "@angular/forms"
import type { ActivatedRoute, Router } from "@angular/router"
import type { MatSnackBar } from "@angular/material/snack-bar"
import { COMMA, ENTER } from "@angular/cdk/keycodes"
import type { MatChipInputEvent } from "@angular/material/chips"
import { PostService } from "../post.service"
//import type { PostService } from "../../../../services/post.service"

@Component({
  selector: "app-post-form",
  templateUrl: "./post-form.component.html",
  styleUrls: ["./post-form.component.scss"],
})
export class PostFormComponent implements OnInit {
  postForm: FormGroup
  isEditMode = false
  postId: string | null = null
  isLoading = false
  isSubmitting = false
  serverErrors: { [key: string]: string } = {}
  readonly separatorKeysCodes = [ENTER, COMMA] as const

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService,
    private snackBar: MatSnackBar,
  ) {
    this.postForm = this.fb.group({
      title: ["", [Validators.required, Validators.maxLength(100)]],
      content: ["", [Validators.required, Validators.minLength(10)]],
      tags: this.fb.array([]),
      published: [true],
    })
  }

  ngOnInit(): void {
    this.postId = this.route.snapshot.paramMap.get("id")
    this.isEditMode = !!this.postId

    if (this.isEditMode && this.postId) {
      this.loadPost(this.postId)
    }
  }

  get tagsArray(): FormArray {
    return this.postForm.get("tags") as FormArray
  }

  loadPost(id: string): void {
    this.isLoading = true

    this.postService.getPostById(id).subscribe({
      next: (post) => {
        // Clear existing tags
        while (this.tagsArray.length) {
          this.tagsArray.removeAt(0)
        }

        // Add each tag to the form array
        post.tags.forEach((tag: string) => {
          this.tagsArray.push(this.fb.control(tag))
        })

        this.postForm.patchValue({
          title: post.title,
          content: post.content,
          published: post.published,
        })

        this.isLoading = false
      },
      error: (error) => {
        console.error("Error loading post", error)
        this.snackBar.open("Failed to load post for editing", "Close", {
          duration: 3000,
        })
        this.isLoading = false
        this.router.navigate(["/blog-forum/post"])
      },
    })
  }

  addTag(event: MatChipInputEvent): void {
    const value = (event.value || "").trim()

    if (value) {
      this.tagsArray.push(this.fb.control(value))
    }

    event.chipInput!.clear()
  }

  removeTag(index: number): void {
    this.tagsArray.removeAt(index)
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.markFormGroupTouched(this.postForm)
      return
    }

    this.isSubmitting = true
    this.serverErrors = {}

    const postRequest = {
      title: this.postForm.value.title,
      content: this.postForm.value.content,
      tags: this.tagsArray.value,
      published: this.postForm.value.published,
    }

    if (this.isEditMode && this.postId) {
      this.updatePost(this.postId, postRequest)
    } else {
      this.createPost(postRequest)
    }
  }

  createPost(postRequest: any): void {
    this.postService.createPost(postRequest).subscribe({
      next: (post) => {
        this.snackBar.open("Post created successfully", "Close", {
          duration: 3000,
        })
        this.isSubmitting = false
        this.router.navigate(["/blog-forum/post", post.id])
      },
      error: (error) => {
        this.handleError(error)
      },
    })
  }

  updatePost(id: string, postRequest: any): void {
    this.postService.updatePost(id, postRequest).subscribe({
      next: (post) => {
        this.snackBar.open("Post updated successfully", "Close", {
          duration: 3000,
        })
        this.isSubmitting = false
        this.router.navigate(["/blog-forum/post", post.id])
      },
      error: (error) => {
        this.handleError(error)
      },
    })
  }

  handleError(error: any): void {
    console.error("Error submitting post", error)
    this.isSubmitting = false

    if (error.error && error.error.errors) {
      const validationErrors = error.error
      validationErrors.errors.forEach((err: any) => {
        this.serverErrors[err.field] = err.message
      })
    } else {
      this.snackBar.open("Failed to save post. Please try again.", "Close", {
        duration: 3000,
      })
    }
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach((control) => {
      control.markAsTouched()

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control)
      }
    })
  }

  cancel(): void {
    if (this.isEditMode && this.postId) {
      this.router.navigate(["/blog-forum/post", this.postId])
    } else {
      this.router.navigate(["/blog-forum/post"])
    }
  }
}
