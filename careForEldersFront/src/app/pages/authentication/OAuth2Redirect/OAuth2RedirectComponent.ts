import {Router} from "@angular/router";
import {Component, OnInit} from "@angular/core";

@Component({
  selector: 'app-oauth2-redirect',
  template: `<p>Redirecting...</p>`
})
export class OAuth2RedirectComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit(): void {
    // Read token from backend response via URL or redirect mechanism
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    if (token) {
      localStorage.setItem('token', token);
      this.router.navigate(['/dashboard']);
    } else {
      this.router.navigate(['/login'], { queryParams: { error: true } });
    }
  }
}
