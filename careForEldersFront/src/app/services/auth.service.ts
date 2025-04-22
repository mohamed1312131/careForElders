import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  token: string;
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

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, data);
  }

  register(data: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/users`, data);
  }

  requestPasswordReset(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/request-reset`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/reset-password`, { token, newPassword });
  }}

