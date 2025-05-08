// test.component.ts
import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.scss']
})
export class TestComponent {
  isDealsMenuOpen = false;
  currentTitle = 'Pipeline';

  constructor(private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.isDealsMenuOpen = this.router.url.includes('/deals');
      this.updateTitle();
    });
  }
  

  toggleDealsMenu() {
    this.isDealsMenuOpen = !this.isDealsMenuOpen;
  }

  private updateTitle() {
    const url = this.router.url;
    if(url.includes('/deals/pipeline')) this.currentTitle = 'Pipeline';
    else if(url.includes('/deals/create')) this.currentTitle = 'Create Deal';
    // Add more title updates as needed
  }
}