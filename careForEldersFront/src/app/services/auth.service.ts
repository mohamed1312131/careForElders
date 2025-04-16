// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map } from 'rxjs';
import { Router } from '@angular/router';

interface User {
  id: number;
  role: string;
  name: string;
  email: string;
  // Add other user properties as needed
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;

  constructor(private http: HttpClient, private router: Router) {
    this.currentUserSubject = new BehaviorSubject<User | null>(
      JSON.parse(localStorage.getItem('currentUser') || 'null')
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  login(email: string, password: string): Observable<User> {
    return this.http.post<{user: User, token: string}>('/api/auth/login', { email, password })
      .pipe(map(response => {
        const user = response.user;
        // Store user details and jwt token in local storage
        localStorage.setItem('currentUser', JSON.stringify(user));
        localStorage.setItem('token', response.token);
        this.currentUserSubject.next(user);
        return user;
      }));
  }

  logout() {
    // Remove user from local storage
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getCurrentUser(): User | null {
    return this.currentUserValue;
  }

  isAuthenticated(): boolean {
    return !!this.currentUserValue;
  }

  isDoctor(): boolean {
    return this.currentUserValue?.role === 'doctor';
  }

  getDoctorId(): number | null {
    if (this.isDoctor()) {
      return this.currentUserValue?.id || null;
    }
    return null;
  
  }
  getCurrentPatientId(): number | null {
    const user = this.currentUserValue;
    if (user && user.role === 'patient') {
      return user.id;
    }
    return null;
  }
}