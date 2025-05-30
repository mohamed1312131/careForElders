import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {PhotoService} from "../../../services/photo.service"
import { AbonnementService } from '../../front-office/subscription/service-abonnement/abonnement.service';
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  registerForm: FormGroup;
  tokenForm: FormGroup;
  showVerification = false;
  loading = false;
  verifying = false;
  verified = false;
  errorMessage: string = '';
  user: any;
  private successMessage: string='';
  constructor(
    private abonnementsrvice:AbonnementService ,
    private fb: FormBuilder, 
    private http: HttpClient,
     private router: Router ,
     private PhotoService:PhotoService) {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: [''],
      birthDate: ['', Validators.required],
      profileImage: [],
      role: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordsMatch });

    this.tokenForm = this.fb.group({
      token: ['', Validators.required]
    });
  }

  passwordsMatch(group: FormGroup) {
    const pass = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return pass === confirm ? null : { passwordMismatch: true };
  }

  onSubmit() {
    if (this.registerForm.invalid) return;
    this.loading = true;

    this.http.post('http://localhost:8081/users', this.registerForm.value).subscribe({
      next: (x) => {
        this.showVerification = true;
        this.loading = false;
        this.user = x;
        console.log(this.user)
         this.abonnementsrvice.assignDefaultPlan(this.user.id).subscribe({
        next: (res) => {
          console.log('✅ Default subscription assigned:', res);
        },
        error: (err) => {
          console.error('❌ Error assigning default subscription:', err);
        }
      });

    },
    error: (err) => {
      console.error(err);
      this.loading = false;
      this.errorMessage = err?.error || 'Something went wrong';
    }
  });
  }

  verifyToken() {
    const token = this.tokenForm.get('token')?.value;
    this.verifying = true;
    this.verified = false;
    this.errorMessage = '';

    this.http.get(`http://localhost:8081/auth/verify?token=${token}`, { responseType: 'text' }).subscribe({
      next: (res) => {
        this.verifying = false;
        this.verified = true;
        this.successMessage = res; // capture the text response like "✅ Email verified! You can now login."
        setTimeout(() => {
          this.router.navigate(['/admin/authentication/login']);
        }, 2000);
      },
      error: (err) => {
        this.verifying = false;
        this.errorMessage = err.error || '❌ Invalid or expired verification token';
      }
    });
  }


  onImageSelected(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = () => {
      this.registerForm.patchValue({ profileImageBase64: reader.result }); // 👈 add base64 directly to form
    };

    if (file) reader.readAsDataURL(file);
  }
  photoUrl: string = '';

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.PhotoService.uploadPhoto(file).subscribe({
        next: (res) => {
          this.photoUrl = res.url;
          // 👇 Save the image URL directly into the form
          this.registerForm.patchValue({ profileImage: res.url });
        },
        error: () => alert('Upload failed')
      });
    }
  }


}
