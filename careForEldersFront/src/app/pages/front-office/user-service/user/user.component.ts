import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { UserDialogComponent } from './user-dialog/user-dialog.component';
import {ConfirmDialogComponent} from "./confirm-dialog/confirm-dialog.component"; // adjust path

@Component({
  selector: 'app-users',
  templateUrl: './user.component.html',
  //styleUrls: ['./user.component.scss']
})export class UsersComponent implements OnInit {
  users: any[] = [];
  filteredUsers: any[] = [];
  paginatedUsers: any[] = [];
  isLoading = false;
  searchTerm = '';

  // Pagination
  currentPage = 1;
  pageSize = 5;
  pageSizes = [5, 10, 25, 50];
  totalPages = 1;

  constructor(private userService: UserService, private toastr: ToastrService,  private dialog: MatDialog) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
        this.totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);
        this.updatePage();
        this.isLoading = false;
      },
      error: () => {
        this.toastr.error('Failed to load users.', 'Error');
        this.isLoading = false;
      }
    });
  }

  onDeleteUser(userId: string): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(userId).subscribe({
        next: () => {
          this.toastr.success('User deleted successfully!', 'Success');
          this.loadUsers();
        },
        error: () => {
          this.toastr.error('Failed to delete the user. Please try again.', 'Error');
        }
      });
    }
  }

  onToggleStatus(user: any): void {
    const updatedUser = { ...user, active: !user.active };
    this.userService.updateUser(user.id, updatedUser).subscribe({
      next: () => {
        this.toastr.success(`User ${updatedUser.active ? 'activated' : 'deactivated'}.`);
        this.loadUsers();
      },
      error: () => {
        this.toastr.error('Failed to update user status.', 'Error');
      }
    });
  }
  makeDoctor(user: { id: string; firstName: string; lastName: string; role?: string }): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Role Change',
        message: `Are you sure you want to make ${user.firstName} ${user.lastName} a doctor?`
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        const updatedUser = { ...user, role: 'DOCTOR' };
        this.userService.updateUser(updatedUser.id, updatedUser).subscribe({
          next: () => {
            const index = this.users.findIndex(u => u.id === user.id);
            if (index !== -1) {
              this.users[index].role = 'doctor';
              this.onSearch(); // Refresh the filtered list
            }
          },
          error: (err) => {
            console.error('Error updating user role:', err);
            // Optionally show error message to user
          }
        });
      }
    });
  }
  onSearch(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.firstName.toLowerCase().includes(term) ||
      user.lastName.toLowerCase().includes(term) ||
      user.email.toLowerCase().includes(term)
    );
    this.currentPage = 1;
    this.updatePage();
  }

  updatePage(): void {
    this.totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedUsers = this.filteredUsers.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.updatePage();
  }

  totalPagesArray(): number[] {
    return Array(this.totalPages)
      .fill(0)
      .map((_, i) => i + 1);
  }
  openEditDialog(user: any): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '400px',
      data: {
        user: { ...user },
        isEditMode: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'updated') {
        this.toastr.success('User updated successfully!');
        this.loadUsers();
      }
    });
  }

}
