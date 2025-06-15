import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../../../services/auth.service';
import { Subscription, timer } from 'rxjs';

@Component({
  selector: 'app-otp-modal',
  templateUrl: './otp-modal.component.html',
  styleUrls: ['./otp-modal.component.css']
})
export class OtpModalComponent implements OnInit, OnDestroy {
  otpForm: FormGroup;
  verificationError: string | null = null;
  countdown = 30;
  private countdownSub!: Subscription;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private dialogRef: MatDialogRef<OtpModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { phoneNumber: string }
  ) {
    this.otpForm = this.fb.group({
      digits: this.fb.array([
        ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
        ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
        ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
        ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
        ['', [Validators.required, Validators.pattern(/^[0-9]$/)]],
        ['', [Validators.required, Validators.pattern(/^[0-9]$/)]]
      ])
    });
  }

  get digits(): FormArray {
    return this.otpForm.get('digits') as FormArray;
  }

  ngOnInit(): void {
    this.startCountdown();
  }

  ngOnDestroy(): void {
    if (this.countdownSub) {
      this.countdownSub.unsubscribe();
    }
  }

  startCountdown(): void {
    this.countdownSub = timer(0, 1000).subscribe(() => {
      this.countdown--;
      if (this.countdown === 0) {
        this.dialogRef.close(false);
      }
    });
  }

  handleKeyDown(event: KeyboardEvent, index: number): void {
    if (event.key === 'Backspace' && !this.digits.at(index).value && index > 0) {
      const prevInput = document.querySelector(`input[formControlName="${index - 1}"]`) as HTMLInputElement;
      prevInput.focus();
    }
  }

  handleInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const value = input.value;

    // Auto-focus next input
    if (value.length === 1 && index < 5) {
      const nextInput = document.querySelector(`input[formControlName="${index + 1}"]`) as HTMLInputElement;
      nextInput.focus();
    }

    // Auto-submit when all digits are entered
    if (index === 5 && value && this.otpForm.valid) {
      this.onVerify();
    }
  }

  onVerify(): void {
    if (this.otpForm.invalid || this.isSubmitting || !this.data.phoneNumber) {
      return;
    }

    this.isSubmitting = true;
    this.verificationError = null;

    // Combine all digits to form the OTP
    const otp = this.digits.value.join('');

    this.authService.verifyOtp(this.data.phoneNumber, otp).subscribe({
      next: () => {
        const token = localStorage.getItem('otpToken');
        if (token) {
          localStorage.setItem('token', token);
          localStorage.removeItem('otpToken');
        }
        localStorage.removeItem('otpPhone');
        this.dialogRef.close(true);
      },
      error: () => {
        this.verificationError = 'Invalid OTP. Please try again.';
        this.isSubmitting = false;
        this.resetForm();
      },
      complete: () => {
        this.isSubmitting = false;
      }
    });
  }

  private resetForm(): void {
    this.digits.controls.forEach(control => {
      control.reset();
    });
    // Focus first input
    const firstInput = document.querySelector('input[formControlName="0"]') as HTMLInputElement;
    firstInput.focus();
  }
}
