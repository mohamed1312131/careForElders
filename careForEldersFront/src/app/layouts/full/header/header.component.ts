import {
  Component,
  Output,
  EventEmitter,
  Input,
  ViewEncapsulation,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {Router} from "@angular/router";


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class HeaderComponent {
  @Input() showToggle = true;
  @Input() toggleChecked = false;
  @Output() toggleMobileNav = new EventEmitter<void>();
  @Output() toggleMobileFilterNav = new EventEmitter<void>();
  @Output() toggleCollapsed = new EventEmitter<void>();

  showFiller = false;
  profileImage: string = '/assets/images/profile/user-1.jpg';
   token = localStorage.getItem('token');
  constructor(public dialog: MatDialog, private router: Router) {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.profileImage = user?.profileImage || this.profileImage; // Use dynamic image or fallback
  }
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    this.router.navigate(['']);
  }
  MyProfile() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const userId = user.id;
      console.log("ID LOCAL STORAGE", userId);
      this.router.navigate([`/user/userinfo/${userId}`]);

    } else {
      console.error("No user found in localStorage");
    }
  }

}
