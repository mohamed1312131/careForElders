import { Component, OnInit, OnDestroy } from '@angular/core';

import { NavigationEnd, Router, Event as RouterEvent } from '@angular/router';
import { filter, takeUntil } from 'rxjs/operators'; // For RxJS operators
import { Subject } from 'rxjs'; // For managing subscriptions
import { UserService } from 'src/app/services/user.service';
import {AuthService} from "../../../../../services/auth.service"; // Adjust path as needed

@Component({
  selector: 'app-user-layout',
  templateUrl: './user-layout.component.html',
  styleUrl: './user-layout.component.scss'
})
export class UserLayoutComponent implements OnInit, OnDestroy {
  isPlanMenuOpen = false;
  isPMedicalRecordMenuOpen = false;
  inNutritionmenu = false;
  inMedicalRecordmenu = false;
  isDealsMenuOpen = false;
  isUserMenuOpen = false;
  inUserMenu = false;
  currentTitle = 'Pipeline';
  user: any = null;
  breadcrumbs: Array<{ label: string, url: string }> = [];
  isDoctor: boolean = false;


  private destroy$ = new Subject<void>();
  userId!: string;

  constructor(
    public router: Router,
    private userService: UserService,
    private authService: AuthService,

  ) {}

  ngOnInit(): void {
    this.getUserInfo(); // Fetch user info on initialization
    this.router.events.pipe(

      filter((event: RouterEvent): event is NavigationEnd => event instanceof NavigationEnd),

      takeUntil(this.destroy$)
    ).subscribe((event: NavigationEnd) => {

      this.isDealsMenuOpen = event.urlAfterRedirects.includes('/deals');
      this.updateTitle(event.urlAfterRedirects);
    });


    this.updateTitle(this.router.url);
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        this.userId = user.id; // Assuming `id` is the property name
      } catch (error) {
        console.error('Failed to parse user from localStorage', error);
      }
    } else {
      console.error('No user is stored in localStorage');
    }


  }
  togglePlanMenu(): void {
    this.isPlanMenuOpen = !this.isPlanMenuOpen;
  }

  toggleNutritionMenu() :void {
    this.inNutritionmenu = !this.inNutritionmenu;
  }
  toggleMedicalRecordMenu() :void {
    this.inMedicalRecordmenu = !this.inMedicalRecordmenu;
  }

  toggleUserMenu() :void {
    this.inUserMenu = !this.inUserMenu;
  }

  getUserInfo(): void {
    // Get user ID directly from localStorage
    const userId = localStorage.getItem('user_id');

    if (!userId) {
      console.warn('User ID not found in local storage.');
      return;
    }

    this.userService.getUserById(userId).subscribe({
      next: (data) => {
        this.user = data;
        console.log('✅ User Info:', this.user);
        this.isDoctor = this.user.role === 'DOCTOR';
      },
      error: (err) => {
        console.error('❌ Failed to retrieve user info', err);
      }
    });
  }

  toggleDealsMenu(): void {
    this.isDealsMenuOpen = !this.isDealsMenuOpen;
  }

  private updateTitle(url: string): void {
    if (!url) return;

    // Existing title logic
    if (url.includes('/plan/add-program')) {
      this.currentTitle = 'Add Program';
    } else if (url.includes('/plan/list')) {
      this.currentTitle = 'Plan List';
    }
    else if (url.includes('/user/userProfile/my-plans')) {
      this.currentTitle = 'my plan';}
    else if (url.includes('/user/userProfile/AI')) {
      this.currentTitle = 'AI Assistant';
    } else if (url.includes('/deals/pipeline')) {
      this.currentTitle = 'Pipeline';
    } else if (url.includes('/deals/create')) {
      this.currentTitle = 'Create Deal';
    } else if (url.includes('/user/userProfile')) {
      this.currentTitle = 'User Profile';
    }

    // Update breadcrumbs
    this.breadcrumbs = this.buildBreadcrumbs(url);
  }

  private buildBreadcrumbs(url: string): Array<{ label: string, url: string }> {
    const breadcrumbs = [];
    const urlSegments = url.split('/').filter(segment => segment !== '');
    let accumulatedPath = '';

    // Always start with Home
    breadcrumbs.push({ label: 'Home', url: '/' });

    for (const segment of urlSegments) {
      accumulatedPath += `/${segment}`;

      switch(segment) {
        case 'userProfile':
          breadcrumbs.push({ label: 'User Profile', url: accumulatedPath });
          break;
        case 'plan':
          breadcrumbs.push({ label: 'Plan', url: accumulatedPath });
          break;
        case 'add-program':
          breadcrumbs.push({ label: 'Add Program', url: accumulatedPath });
          break;
        case 'list':
          breadcrumbs.push({ label: 'Plan List', url: accumulatedPath });
          break;
        case 'my plans':
          breadcrumbs.push({ label: 'Plan List', url: accumulatedPath });
          break;
        case 'AI':
          breadcrumbs.push({ label: 'AI Assistant', url: accumulatedPath });
          break;
        case 'deals':
          breadcrumbs.push({ label: 'Deals', url: accumulatedPath });
          break;
        // Add more cases as needed for other routes
      }
    }

    return breadcrumbs;
  }

  logout(): void {
    localStorage.removeItem('token');

    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {

    this.destroy$.next();
    this.destroy$.complete();
  }
  isDoctor2(): boolean {
    return this.authService.getCurrentUserRole() === 'DOCTOR';
  }

  isAdministrator(): boolean {
    console.log(this.authService.getCurrentUserRole());
    return this.authService.getCurrentUserRole() === 'ADMINISTRATOR';
  }


}
