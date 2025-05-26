import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { AbonnementService } from './service-abonnement/abonnement.service';


@Injectable({
  providedIn: 'root'
})
export class SubscriptionGuard implements CanActivate {
  constructor(
    private abonnementService: AbonnementService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const userId = localStorage.getItem('user_id');
    const requiredModule = route.data['modules'];

    // Debugging logs
    console.log('Route Data:', route.data);
    console.log('Required Modules:', requiredModule);

    if (!userId) {
      console.warn('No user ID found. Redirecting to login.');
      this.router.navigate(['/login']);
      return of(false);
    }

    if (!requiredModule) {
      console.error('No module specified in route data');
      this.router.navigate(['/user/userProfile/unauthorized']);
      return of(false);
    }

    return this.abonnementService.getCurrentSubscription(userId).pipe(
      tap(subscription => console.log('Full subscription response:', subscription)),
      map(subscription => {
  const modules = subscription?.features || []; // Corrected from 'modules'
  console.log('User Modules:', modules);

  if (modules.includes(requiredModule)) {
    return true;
  }

  console.warn(`Access denied. Module "${requiredModule}" not included in subscription.`);
  this.router.navigate(['/user/userProfile/unauthorized']);
  return false;
}),
      catchError(error => {
        console.error('Subscription check failed:', error);
        this.router.navigate(['/user/userProfile/unauthorized']);
        return of(false);
      })
    );
  }
}