import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss'
})
export class HomePageComponent {
  isLoggedIn = false;

  constructor(private router: Router) {
    this.isLoggedIn = !!localStorage.getItem('token'); // Adjust based on your auth logic
  }

  logout() {
    localStorage.removeItem('token'); // Or your auth token name
    this.isLoggedIn = false;
    this.router.navigate(['']);
  }
}
