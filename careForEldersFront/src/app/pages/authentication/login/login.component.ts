import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService  } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  loggingIn = false;
  loginError: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authApi: AuthService  ,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onLogin(): void {
    if (this.loginForm.invalid) return;

    this.loggingIn = true;
    this.loginError = null;

    const { email, password } = this.loginForm.value;

    this.authApi.login({ email, password }).subscribe({
      next: (res: any) => {
        localStorage.setItem('token', res.token);
        this.router.navigate(['/admin/dashboard']);
      },
      error: (err: any) => {
        console.error('❌ Login failed', err);

        if (err.status === 403) {
          this.loginError = '⚠️ Please verify your email before logging in.';
        } else if (err.status === 401) {
          this.loginError = '❌ Invalid email or password.';
        } else {
          this.loginError = '❌ An unexpected error occurred.';
        }

        this.loggingIn = false;
      }
    });
  }
  requestReset(): void {
    const email = this.loginForm.get('email')?.value;
    if (!email) {
      this.loginError = 'Please enter your email to reset your password.';
      return;
    }

    this.authApi.requestPasswordReset(email).subscribe({
      next: () => {
        this.loginError = '✔️ Reset instructions sent to your email.';
      },
      error: () => {
        this.loginError = '❌ Failed to send reset instructions.';
      }
    });
  }
}
