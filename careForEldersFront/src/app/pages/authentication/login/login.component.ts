import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService  } from '../../../services/auth.service';
import {MatDialog} from "@angular/material/dialog";
import {OtpModalComponent} from "../otp-modal/otp-modal.component";
import {ChooseMethodModalComponent} from "../choose-method-modal/choose-method-modal.component";
declare const google: any;

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
    private router: Router,
    private dialog: MatDialog

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
        if (res.requires2FA) {
          const tempToken = res.tempToken;
          const userId = res.userId;

          localStorage.setItem('otpToken', tempToken);
          localStorage.setItem('otpUserId', userId);

          this.authApi.getUserById(userId).subscribe({
            next: (user: any) => {
              localStorage.setItem('user', JSON.stringify(user));

              this.dialog.open(ChooseMethodModalComponent, {
                data: { email: user.email, phoneNumber: user.phoneNumber },
                width: '400px',
                disableClose: true
              }).afterClosed().subscribe((method: 'sms' | 'email' | null) => {
                if (!method) {
                  this.loginError = '⚠️ Two-factor method selection was cancelled.';
                  this.loggingIn = false;
                  return;
                }

                const destination = method === 'sms' ? user.phoneNumber : user.email;

                this.authApi.sendOtp(destination, method).subscribe({
                  next: () => {
                    this.dialog.open(OtpModalComponent, {
                      data: { phoneNumber: destination, method },
                      width: '400px',
                      disableClose: true
                    }).afterClosed().subscribe(success => {
                      if (success) {
                        this.router.navigate([`/user/userProfile/userinfo/${userId}`]);
                      } else {
                        this.loginError = '⏱️ OTP expired or verification failed.';
                      }
                      this.loggingIn = false;
                    });
                  },
                  error: () => {
                    this.loginError = `❌ Failed to send OTP via ${method.toUpperCase()}.`;
                    this.loggingIn = false;
                  }
                });
              });
            },
            error: () => {
              this.loginError = '❌ Failed to retrieve user data.';
              this.loggingIn = false;
            }
          });

        } else {
          // No 2FA required
          const token = res.token;
          const user = res.user;

          if (!token || !user) {
            throw new Error('❌ Invalid response structure for normal login.');
          }

          localStorage.setItem('token', token);
          localStorage.setItem('user', JSON.stringify(user));
          this.router.navigate([`/user/userProfile/userinfo/${user.id}`]);
          this.loggingIn = false;
        }
      },
      error: (err: any) => {
        console.error('❌ Login failed', err);
        this.loginError =
          err.status === 403 ? '⚠️ Please verify your email before logging in.' :
            err.status === 401 ? '❌ Invalid email or password.' :
              '❌ An unexpected error occurred.';
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
  loginWith(provider: 'google' | 'facebook') {
    const redirectUri = encodeURIComponent(window.location.origin + '/oauth2/redirect');
    window.location.href = `http://localhost:8081/oauth2/authorization/${provider}?redirect_uri=${redirectUri}`;
  }

  ngAfterViewInit(): void {
    // Wait until Google Identity is available
    if (typeof google !== 'undefined') {
      google.accounts.id.initialize({
        client_id: '514678328805-t37tceg3kfq3q5vjr56gnv689dn74rmm.apps.googleusercontent.com',
        callback: (response: any) => this.handleGoogleCredential(response)
      });


    }
  }
  handleGoogleCredential(response: any): void {
    console.log('✅ Google credential:', response.credential);
    // You should send `response.credential` (JWT) to your backend for verification and login
  }
}
