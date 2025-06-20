import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../../services/user.service'; // Service to handle backend API calls
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr'; // For displaying notifications
import { PhotoService } from '../../../../services/photo.service';
import {MatCheckboxChange} from "@angular/material/checkbox"; // Your PhotoService

@Component({
  selector: 'app-userinfo',
  templateUrl: './userinfo.component.html',
  styleUrls: ['./userinfo.component.scss']
})
export class UserinfoComponent implements OnInit {
  userForm!: FormGroup;
  userId: string = ''; // User ID from the route or logged-in user
  isLoading: boolean = false;
  roles: string[] = ['NURSE', 'USER', 'ADMINISTRATOR', 'DOCTOR'];
  originalUserData: any = {}; // Store the original user data for comparison

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private route: ActivatedRoute,
    private toastr: ToastrService,
    private photoService: PhotoService // Inject your PhotoService
  ) {}

  ngOnInit(): void {
    // Initialize the form
    this.userForm = this.fb.group({
      id: [{ value: '', disabled: true }],
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(255)]],
      password: ['', [Validators.minLength(8), Validators.maxLength(100)]],
      birthDate: [''],
      profileImage: [''],
      phoneNumber: ['', [Validators.pattern(/^\+?[0-9]{8}$/)]],
      role: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      twoFactorEnabled: []
    });


    // Get the user ID from the URL or other sources
    this.userId = this.route.snapshot.paramMap.get('id') || '';

    // Load user info into form
    this.loadUserInfo();
  }

  // Load user data into the form
  loadUserInfo(): void {
    if (this.userId) {
      this.isLoading = true;
      this.userService.getUserById(this.userId).subscribe({
        next: (user) => {
          this.originalUserData = user; // Store the original data for comparison
          this.userForm.patchValue(user); // Populate the form with user data
          this.isLoading = false;
        },
        error: () => {
          this.toastr.error('Failed to load user information.', 'Error');
          this.isLoading = false;
        },
      });
    }
  }

  // Handle image selection
  onImageSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.isLoading = true;
      this.photoService.uploadPhoto(file).subscribe({
        next: (response) => {
          const imageUrl = response.url; // The URL from the backend (which is sent to Cloudinary)
          this.userForm.patchValue({ profileImage: imageUrl });
          this.isLoading = false;
          this.toastr.clear();
          this.toastr.success('Profile image updated successfully!', 'Success');
        },
        error: () => {
          this.isLoading = false;
          this.toastr.error('Failed to upload image. Please try again.', 'Error');
        },
      });
    }
  }

  // Extract changed fields only
  getChangedFields(): any {
    const updatedData: any = {};
    const currentFormData = this.userForm.getRawValue();

    for (const key in currentFormData) {
      if (!currentFormData.hasOwnProperty(key)) continue;

      const currentValue = currentFormData[key];
      const originalValue = this.originalUserData[key];

      if (currentValue !== originalValue) {
        updatedData[key] = currentValue;
      }
    }
console.log(updatedData);
    return updatedData;
  }

  // Update user data
  onUpdate(): void {
    console.log('Updating user data...');
    console.log('Form data:', this.userForm.getRawValue());
    if (this.userForm.valid) {
      console.log('Form is valid');
      const changedFields = this.getChangedFields();
      console.log('Changed fields:', changedFields);
      if (Object.keys(changedFields).length === 0) {
        this.toastr.info('No changes detected.', 'Nothing to Save');
        console.log('No changes detected');
        return;
      }

      this.isLoading = true;
      console.log('Updating user...',changedFields);
      this.userService.updateUser(this.userId, changedFields).subscribe({
        next: () => {
          this.toastr.success('User information updated successfully.', 'Success ✅');
          this.originalUserData = { ...this.originalUserData, ...changedFields };

          this.isLoading = false;
        },
        error: (error) => {
          this.isLoading = false;
          if (error.status === 400 && error.error && error.error.errors) {
            this.handleValidationErrors(error.error.errors);
          } else {
            this.toastr.error('Failed to update user. Please try again.', 'Error ❌');
          }
        }
      });
    } else {
      this.toastr.warning('Please correct the errors before submitting.', 'Validation Warning ⚠️');
    }
  }

  private handleValidationErrors(errors: any): void {
    for (const field in errors) {
      if (this.userForm.contains(field)) {
        const control = this.userForm.get(field);
        control?.setErrors({ serverError: errors[field].join(', ') });
      }
    }
    this.toastr.error('Please resolve the errors in the form.', 'Validation Error');
  }
  onToggle2FA(event: MatCheckboxChange): void {
    const enable = event.checked;

    const action = enable ? 'enable' : 'disable';
    const confirmMessage = `Are you sure you want to ${action} Two-Factor Authentication?`;

    const userConfirmed = confirm(confirmMessage); // Basic browser confirm

    if (userConfirmed) {
      this.userForm.patchValue({twoFactorEnabled: enable});
    } else {
      // Revert the checkbox to the previous value
      event.source.checked = !enable;
    }

  }
}
