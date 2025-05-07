import { Component, OnInit } from '@angular/core';
import { navItems } from './sidebar-data';
import { NavService } from '../../../services/nav.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent implements OnInit {
  navItems = navItems;
  openNavItem: string | null = null; // Track which item is open

  constructor(public navService: NavService) {}

  ngOnInit(): void {}
  toggleDropdown(item: string) {
    this.openNavItem = this.openNavItem === item ? null : item;
  }
}
