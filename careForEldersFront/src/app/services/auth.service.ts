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
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, loginRequest)
      .pipe(
      map((response: any) => {
        const token = response.data.token;
        const user = response.data.user;

        if (token && user) {
          // Save token and user data to local storage
          localStorage.setItem('token', token);
          localStorage.setItem('user', JSON.stringify(user));
          return { token, user }; // Optionally return for further usage
        } else {
          throw new Error('Invalid response structure');
        }
      }),
      catchError((error: any) => {
        // Log the error and rethrow it for the component to handle
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
  }}

