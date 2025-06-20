import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar // 👈 pour afficher un message
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRoles: string[] = route.data['roles'];
    const userRole = this.authService.getCurrentUserRole();

    if (expectedRoles.includes(userRole)) {
      return true;
    }

    // ❌ Redirection + message
    this.snackBar.open('Access denied: You are not authorized to view this page.', 'Close', {
      duration: 4000,
      verticalPosition: 'top',
    });

    this.router.navigate(['/user/userProfile']);
    return false;
  }
}
