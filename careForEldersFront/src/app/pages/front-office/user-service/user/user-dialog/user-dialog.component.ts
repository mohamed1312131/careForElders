import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserService } from '../../../../../services/user.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-user-dialog',
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.scss'],
})
export class UserDialogComponent implements OnInit {
  userForm!: FormGroup;
  roles: string[] = ['Admin', 'User', 'Manager', 'Editor'];
  profileImage: string | null = null;
  isImageUploading = false;

  constructor(
    private fb: FormBuilder,
    protected dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      user: {
        id: number;
        firstName: string;
        lastName: string;
        email: string;
        role: string;
        profileImage?: string;
        status?: string;
        twoFactorAuth?: boolean;
      }
    },
    private userService: UserService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    if (this.data.user.profileImage) {
      this.profileImage = this.data.user.profileImage;
    }
  }

  initializeForm(): void {
    this.userForm = this.fb.group({
      id: [this.data.user.id],
      firstName: [this.data.user.firstName, [Validators.required, Validators.maxLength(50)]],
      lastName: [this.data.user.lastName, [Validators.required, Validators.maxLength(50)]],
      email: [this.data.user.email, [Validators.required, Validators.email]],
      role: [this.data.user.role || 'User', [Validators.required]],
      status: [this.data.user.status || 'active', [Validators.required]],
      twoFactorAuth: [this.data.user.twoFactorAuth || false],
      profileImage: [this.data.user.profileImage || null]
    });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.match(/image\/(jpeg|png|gif)/)) {
      this.toastr.warning('Please upload a valid image file (JPEG, PNG, GIF)', 'Invalid File');
      return;
    }

    // Validate file size (max 2MB)
    if (file.size > 2097152) {
      this.toastr.warning('Image size should not exceed 2MB', 'File Too Large');
      return;
    }

    this.isImageUploading = true;
    const reader = new FileReader();

    reader.onload = (e: any) => {
      this.profileImage = e.target.result;
      this.userForm.patchValue({ profileImage: this.profileImage });
      this.isImageUploading = false;
    };

    reader.onerror = () => {
      this.toastr.error('Error reading image file', 'Upload Error');
      this.isImageUploading = false;
    };

    reader.readAsDataURL(file);
  }

  onSave(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      this.toastr.warning('Please fill all required fields correctly', 'Validation Error');
      return;
    }

    const updatedUser = this.userForm.value;
    this.userService.updateUser(updatedUser.id, updatedUser).subscribe({
      next: () => {
        this.toastr.success('User updated successfully!', 'Success');
        this.dialogRef.close({ action: 'updated', user: updatedUser });
      },
      error: (err) => {
        console.error(err);
        const errorMessage = err.error?.message || 'Failed to update user';
        this.toastr.error(errorMessage, 'Error');
      }
    });
  }

  onDelete(): void {
    if (confirm('Are you sure you want to delete this user?')) {
      // Ensure we're passing a string ID if that's what the service expects
      const userId = this.data.user.id.toString(); // Convert to string if needed

      this.userService.deleteUser(userId).subscribe({
        next: () => {
          this.toastr.success('User deleted successfully', 'Success');
          this.dialogRef.close({ action: 'deleted' });
        },
        error: (err) => {
          console.error(err);
          this.toastr.error('Failed to delete user', 'Error');
        }
      });
    }
  }

  getInitials(): string {
    const first = this.userForm?.value?.firstName?.[0] || '';
    const last = this.userForm?.value?.lastName?.[0] || '';
    return `${first}${last}`.toUpperCase();
  }

  getStatusColor(): string {
    const status = this.userForm?.value?.status;
    switch (status) {
      case 'active': return 'green';
      case 'inactive': return 'red';
      case 'pending': return 'orange';
      default: return 'gray';
    }
  }
}
