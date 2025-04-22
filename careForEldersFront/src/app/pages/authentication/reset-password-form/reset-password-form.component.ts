import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-reset-password-form',
  templateUrl: './reset-password-form.component.html',
  styleUrls: ['./reset-password-form.component.scss']
})
export class ResetPasswordFormComponent implements OnInit {
  resetForm: FormGroup;
  loading = false;
  message = '';
  success = false;
  token: string | null = null;
   userId = localStorage.getItem('user');
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.resetForm = this.fb.group(
      {
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');

    if (!this.token) {
      this.message = 'Invalid or missing reset token. Please request a new password reset link.';
      this.success = false;
      return;
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return newPassword === confirmPassword ? null : { mismatch: true };
  }

  onSubmit(): void {
    if (this.resetForm.invalid || !this.token) return;

    const { newPassword } = this.resetForm.value;

    this.loading = true;
    this.message = '';

    this.authService.resetPassword(this.token, newPassword).subscribe({
      next: () => {
        this.success = true;
        this.message = 'Password reset successfully. You can now log in with your new password.';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/admin/authentication/login']), 3000);
      },
      error: (err) => {
        this.success = false;
        this.message = err.error || 'Failed to reset password. The link may have expired.';
        this.loading = false;
      }
    });
  }
}
