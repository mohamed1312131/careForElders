import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatChipInputEvent } from '@angular/material/chips';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { PostService } from '../post.service';
import { Post, PostRequest, PostImage } from '../models/post.model';

interface FileUploadResult {
  fileName: string;
  originalFileName: string;
  fileUrl: string;
  fileType: string;
  fileSize: number;
  filePath: string;
}

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['./post-form.component.scss']
})
export class PostFormComponent implements OnInit {
  postForm: FormGroup;
  isLoading = false;
  isEditMode = false;
  postId: string | null = null;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  
  // Image upload properties
  featuredImage: File | null = null;
  featuredImagePreview: string | null = null;
  additionalImages: File[] = [];
  additionalImagePreviews: string[] = [];
  isUploadingImages = false;
  
  // Image validation
  maxFileSize = 5 * 1024 * 1024; // 5MB
  allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
  maxAdditionalImages = 5;
  
  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      content: ['', [Validators.required, Validators.minLength(20)]],
      tags: this.fb.array([]),
      // Image metadata fields
      imageAltText: [''],
      imageCaption: ['']
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.postId = params['id'];
      this.isEditMode = !!this.postId;
      
      if (this.isEditMode && this.postId) {
        this.loadPost(this.postId);
      }
    });
  }

  get title() { return this.postForm.get('title'); }
  get content() { return this.postForm.get('content'); }
  get tags() { return this.postForm.get('tags') as FormArray; }
  get imageAltText() { return this.postForm.get('imageAltText'); }
  get imageCaption() { return this.postForm.get('imageCaption'); }

  loadPost(id: string): void {
    this.isLoading = true;
    this.postService.getPostById(id).subscribe({
      next: (post) => {
        // Reset the tags FormArray
        while (this.tags.length) {
          this.tags.removeAt(0);
        }
        
        // Add each tag to the FormArray
        if (post.tags && post.tags.length > 0) {
          post.tags.forEach(tag => {
            this.tags.push(this.fb.control(tag));
          });
        }
        
        // Update the form values
        this.postForm.patchValue({
          title: post.title,
          content: post.content,
          imageAltText: post.imageAltText || '',
          imageCaption: post.imageCaption || ''
        });
        
        // Load existing images
        if (post.featuredImageUrl) {
          this.featuredImagePreview = post.featuredImageUrl;
        }
        
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading post', error);
        this.snackBar.open('Failed to load post', 'Close', {
          duration: 3000
        });
        this.isLoading = false;
        this.router.navigate(['/user/userProfile/post']);
      }
    });
  }

  // Image Upload Methods
  onFeaturedImageSelected(event: any): void {
    const file = event.target.files[0];
    if (file && this.validateImageFile(file)) {
      this.featuredImage = file;
      this.createImagePreview(file, (preview) => {
        this.featuredImagePreview = preview;
      });
    }
  }

  onAdditionalImagesSelected(event: any): void {
    const files = Array.from(event.target.files) as File[];
    
    if (this.additionalImages.length + files.length > this.maxAdditionalImages) {
      this.snackBar.open(`Maximum ${this.maxAdditionalImages} additional images allowed`, 'Close', {
        duration: 3000
      });
      return;
    }
    
    files.forEach(file => {
      if (this.validateImageFile(file)) {
        this.additionalImages.push(file);
        this.createImagePreview(file, (preview) => {
          this.additionalImagePreviews.push(preview);
        });
      }
    });
  }

  validateImageFile(file: File): boolean {
    if (!this.allowedTypes.includes(file.type)) {
      this.snackBar.open('Invalid file type. Only images are allowed.', 'Close', {
        duration: 3000
      });
      return false;
    }
    
    if (file.size > this.maxFileSize) {
      this.snackBar.open('File size too large. Maximum 5MB allowed.', 'Close', {
        duration: 3000
      });
      return false;
    }
    
    return true;
  }

  createImagePreview(file: File, callback: (preview: string) => void): void {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      callback(e.target.result);
    };
    reader.readAsDataURL(file);
  }

  removeFeaturedImage(): void {
    this.featuredImage = null;
    this.featuredImagePreview = null;
    this.postForm.patchValue({
      imageAltText: '',
      imageCaption: ''
    });
  }

  removeAdditionalImage(index: number): void {
    this.additionalImages.splice(index, 1);
    this.additionalImagePreviews.splice(index, 1);
  }

  // Drag and Drop functionality
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }

  onDrop(event: DragEvent, type: 'featured' | 'additional'): void {
    event.preventDefault();
    event.stopPropagation();
    
    const files = Array.from(event.dataTransfer?.files || []);
    
    if (type === 'featured' && files.length > 0) {
      const file = files[0];
      if (this.validateImageFile(file)) {
        this.featuredImage = file;
        this.createImagePreview(file, (preview) => {
          this.featuredImagePreview = preview;
        });
      }
    } else if (type === 'additional') {
      files.forEach(file => {
        if (this.additionalImages.length < this.maxAdditionalImages && this.validateImageFile(file)) {
          this.additionalImages.push(file);
          this.createImagePreview(file, (preview) => {
            this.additionalImagePreviews.push(preview);
          });
        }
      });
    }
  }

  // Tag methods
  addTag(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    
    if (value) {
      this.tags.push(this.fb.control(value));
    }
    
    event.chipInput!.clear();
  }

  removeTag(index: number): void {
    this.tags.removeAt(index);
  }

  // Form submission
  async onSubmit(): Promise<void> {
    if (this.postForm.invalid) {
      return;
    }
    
    this.isLoading = true;
    this.isUploadingImages = true;
    
    try {
      // Upload images first
      let featuredImageResult: FileUploadResult | null = null;
      let additionalImageResults: FileUploadResult[] = [];
      
      if (this.featuredImage) {
        featuredImageResult = await this.uploadSingleImage(this.featuredImage);
      }
      
      if (this.additionalImages.length > 0) {
        additionalImageResults = await this.uploadMultipleImages(this.additionalImages);
      }
      
      this.isUploadingImages = false;
      
      // Prepare post request
      const postRequest: PostRequest = {
        title: this.title?.value,
        content: this.content?.value,
        tags: this.tags.value,
        featuredImageUrl: featuredImageResult?.fileUrl,
        featuredImageName: featuredImageResult?.fileName,
        featuredImageType: featuredImageResult?.fileType,
        featuredImageSize: featuredImageResult?.fileSize,
        imageAltText: this.imageAltText?.value,
        imageCaption: this.imageCaption?.value,
        additionalImages: additionalImageResults.map((result, index) => ({
          url: result.fileUrl,
          fileName: result.fileName,
          fileType: result.fileType,
          fileSize: result.fileSize,
          order: index,
          altText: '',
          caption: ''
        }))
      };
      
      if (this.isEditMode && this.postId) {
        this.updatePost(this.postId, postRequest);
      } else {
        this.createPost(postRequest);
      }
      
    } catch (error) {
      console.error('Error uploading images', error);
      this.snackBar.open('Failed to upload images', 'Close', {
        duration: 3000
      });
      this.isLoading = false;
      this.isUploadingImages = false;
    }
  }

  private uploadSingleImage(file: File): Promise<FileUploadResult> {
    return new Promise((resolve, reject) => {
      this.postService.uploadImage(file).subscribe({
        next: (result) => resolve(result),
        error: (error) => reject(error)
      });
    });
  }

  private uploadMultipleImages(files: File[]): Promise<FileUploadResult[]> {
    return new Promise((resolve, reject) => {
      this.postService.uploadMultipleImages(files).subscribe({
        next: (results) => resolve(results),
        error: (error) => reject(error)
      });
    });
  }

  createPost(postRequest: PostRequest): void {
    this.postService.createPost(postRequest).subscribe({
      next: (post) => {
        this.snackBar.open('Post created successfully', 'Close', {
          duration: 3000
        });
        this.isLoading = false;
        this.router.navigate(['/user/userProfile/post', post.id]);
      },
      error: (error) => {
        console.error('Error creating post', error);
        this.snackBar.open('Failed to create post', 'Close', {
          duration: 3000
        });
        this.isLoading = false;
      }
    });
  }

  updatePost(id: string, postRequest: PostRequest): void {
    this.postService.updatePost(id, postRequest).subscribe({
      next: (post) => {
        this.snackBar.open('Post updated successfully', 'Close', {
          duration: 3000
        });
        this.isLoading = false;
        this.router.navigate(['/user/userProfile/post', post.id]);
      },
      error: (error) => {
        console.error('Error updating post', error);
        this.snackBar.open('Failed to update post', 'Close', {
          duration: 3000
        });
        this.isLoading = false;
      }
    });
  }

  cancel(): void {
    if (this.isEditMode && this.postId) {
      this.router.navigate(['/user/userProfile/post', this.postId]);
    } else {
      this.router.navigate(['/user/userProfile/post']);
    }
  }

  canDeactivate(): boolean {
    return !this.postForm.dirty || confirm('You have unsaved changes. Do you really want to leave?');
  }
}