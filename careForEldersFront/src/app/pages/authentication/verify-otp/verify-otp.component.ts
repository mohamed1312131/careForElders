import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service'

@Component({
  selector: 'app-verify-otp',
  templateUrl: './verify-otp.component.html',
  styleUrls: ['./verify-otp.component.css']
})
export class VerifyOtpComponent {
  otpForm: FormGroup;
  verificationError: string | null = null;
  phoneNumber: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
    });

    this.phoneNumber = localStorage.getItem('otpPhone');
    if (!this.phoneNumber) {
      this.router.navigate(['/auth/login']);
    }
  }

  verifyOtp(): void {
    if (!this.phoneNumber || this.otpForm.invalid) return;

    const otp = this.otpForm.value.otp;
    this.authService.verifyOtp(this.phoneNumber, otp).subscribe({
      next: (_res: any) => {
        const token = localStorage.getItem('otpToken');
        if (token) {
          localStorage.setItem('token', token);
          localStorage.removeItem('otpToken');
        }

        localStorage.removeItem('otpPhone');
        this.router.navigate(['/user/userProfile']); // Adjust this to your dashboard route
      },
      error: () => {
        this.verificationError = 'Invalid OTP. Please try again.';
      }
    });
  }
}
