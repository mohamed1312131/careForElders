import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  templateUrl: './unauthorized.component.html',
  styleUrl: './unauthorized.component.scss'
})
export class UnauthorizedComponent {
  requiredModule: string | null = null;
   countdown: number = 10;
  private countdownInterval: any;

  constructor(private route: ActivatedRoute, private router: Router) {
    this.route.queryParamMap.subscribe(params => {
      this.requiredModule = params.get('requiredModules');
    });
  
  this.startCountdown();
  }

  startCountdown(): void {
    this.countdownInterval = setInterval(() => {
      this.countdown--;
      if (this.countdown === 0) {
        clearInterval(this.countdownInterval);
        this.router.navigate(['/user/userProfile/search']);
      }
    }, 1000);
  }
  
}

