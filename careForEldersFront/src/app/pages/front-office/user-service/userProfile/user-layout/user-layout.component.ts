import { Component, OnInit, OnDestroy } from '@angular/core';

import { NavigationEnd, Router, Event as RouterEvent } from '@angular/router';
import { filter, takeUntil } from 'rxjs/operators'; // For RxJS operators
import { Subject } from 'rxjs'; // For managing subscriptions
import { UserService } from 'src/app/services/user.service'; // Adjust path as needed

@Component({
  selector: 'app-user-layout',
  templateUrl: './user-layout.component.html',
  styleUrl: './user-layout.component.scss' 
})
export class UserLayoutComponent implements OnInit, OnDestroy {
  isDealsMenuOpen = false;
  currentTitle = 'Pipeline'; 
  user: any = null; 

  
  private destroy$ = new Subject<void>();

  constructor(
    public router: Router, 
    private userService: UserService
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

    
    if (url.includes('/user/userProfile/AI')) {
      this.currentTitle = 'AI Assistant';
    } else if (url.includes('/deals/pipeline')) { 
      this.currentTitle = 'Pipeline';
    } else if (url.includes('/deals/create')) {
      this.currentTitle = 'Create Deal';
    } else if (url.includes('/user/userProfile')) { 
      this.currentTitle = 'User Profile';
    }
    
  }

  logout(): void {
    localStorage.removeItem('token');
   
    this.router.navigate(['/login']); 
  }

  ngOnDestroy(): void {
    
    this.destroy$.next();
    this.destroy$.complete();
  }
}