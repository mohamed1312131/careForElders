import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent {
  resetForm: FormGroup;
  loading = false;
  message = '';
  success = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.resetForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.resetForm.invalid) return;

    this.loading = true;
    this.message = '';

    const email = this.resetForm.value.email;

    this.authService.requestPasswordReset(email).subscribe({
      next: () => {
        this.success = true;
        this.message = 'Password reset link sent to your email. Please check your inbox.';
        this.loading = false;
      },
      error: (err) => {
        this.success = false;
        this.message = this.extractErrorMessage(err, 'Failed to send reset link. Please try again.');
        this.loading = false;
      }

    });
  }
  private extractErrorMessage(err: any, fallback: string): string {
    if (!err) return fallback;
    if (typeof err.error === 'string') return err.error;
    if (typeof err.error?.message === 'string') return err.error.message;
    if (typeof err.message === 'string') return err.message;
    return fallback;
  }

}
