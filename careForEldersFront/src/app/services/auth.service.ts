import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {catchError, map, Observable, throwError} from 'rxjs';
import { tap } from 'rxjs/operators';


interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  token: string;
  user:string;
}

interface ResetPasswordRequest {
  email: string;
}

interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword?: string;
  phoneNumber?: string;
  role: string;
  birthDate: string;
  profileImageBase64?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService  {
  private apiUrl = 'http://localhost:8081';

  constructor(private http: HttpClient) {}

  login(loginRequest: LoginRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/login`, loginRequest).pipe(
      map((response: any) => {
        if (response.requires2FA) {
          // Store temp token and user ID for OTP flow
          localStorage.setItem('otpToken', response.tempToken);
          localStorage.setItem('otpUserId', response.userId);
          return {
            requires2FA: true,
            tempToken: response.tempToken,
            userId: response.userId
          };
        } else if (response.token && response.user) {
          // Normal login
          localStorage.setItem('token', response.token);
          localStorage.setItem('user', JSON.stringify(response.user));
          return {
            token: response.token,
            user: response.user,
            requires2FA: false
          };
        } else {
          throw new Error('Invalid response structure');
        }
      }),
      catchError((error: any) => {
        console.error('Error during login:', error);
        return throwError(() => error);
      })
    );
  }







  register(data: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/users`, data);
  }
  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/users`);
  }
  getUserById(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/users/${id}`);
  }
  deleteUser(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/users/${id}`);
  }
  updateUser(id: string, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/users/${id}`, data);
  }


  requestPasswordReset(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/request-reset`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/reset-password`, { token, newPassword });
  }

  sendOtp(destination: string, method: "sms" | "email"): Observable<any> {
    if (method === 'sms') {
      return this.http.post(`${this.apiUrl}/auth/send-otp`, { phone: destination }, { responseType: 'text' as 'json' });
    } else {
      return this.http.post(`${this.apiUrl}/auth/send-email-otp`, { email: destination }, { responseType: 'text' as 'json' });
    }
  }


  verifyOtp(phone: string, otp: string) {
    return this.http.post(`${this.apiUrl}/auth/verify-otp`, { phone, otp }, { responseType: 'text' as 'json' });
  }

  getCurrentUserRole(): string {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    return user.roles || '';
  }

}

