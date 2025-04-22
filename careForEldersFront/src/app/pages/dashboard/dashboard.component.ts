import { Component, ViewEncapsulation, ViewChild,OnInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { AuthService } from 'src/app/services/auth.service';
import { UserDialogComponent } from './user-dialog/user-dialog.component';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-dashboard',
  templateUrl: '/dashboard.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class AppDashboardComponent {
  displayedColumns: string[] = ['name', 'email', 'role', 'status', 'actions'];
  users: any[] = [];
  filteredUsers: any[] = [];
  searchTerm: string = '';
  isLoading: boolean = false;
  sortDirection: 'asc' | 'desc' = 'asc';
  currentSortColumn: string = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private authService: AuthService,
    private dialog: MatDialog,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.authService.getAllUsers().subscribe({
      next: (res: any) => {
        this.users = res;
        this.filteredUsers = [...this.users];
        this.isLoading = false;
      },
      error: (err) => {
        this.toastr.error('Failed to load users', 'Error');
        this.isLoading = false;
        console.error('Error loading users:', err);
      }
    });
  }

  applyFilter(): void {
    if (!this.searchTerm) {
      this.filteredUsers = [...this.users];
      return;
    }

    const searchTerm = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.firstName.toLowerCase().includes(searchTerm) ||
      user.lastName.toLowerCase().includes(searchTerm) ||
      user.email.toLowerCase().includes(searchTerm) ||
      user.role.toLowerCase().includes(searchTerm)
    );
  }

  sortData(column: string): void {
    if (this.currentSortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortColumn = column;
      this.sortDirection = 'asc';
    }

    this.filteredUsers.sort((a, b) => {
      const valueA = column === 'name'
        ? `${a.firstName} ${a.lastName}`.toLowerCase()
        : a[column].toLowerCase();
      const valueB = column === 'name'
        ? `${b.firstName} ${b.lastName}`.toLowerCase()
        : b[column].toLowerCase();

      return this.sortDirection === 'asc'
        ? valueA.localeCompare(valueB)
        : valueB.localeCompare(valueA);
    });
  }

  openUserDialog(user?: any): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '600px',
      data: user || null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  editUser(user: any): void {
    this.openUserDialog(user);
  }

  viewUserDetails(user: any): void {
    // Implement detailed view logic
    console.log('View user details:', user);
  }

  deleteUser(userId: string): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Delete',
        message: 'Are you sure you want to delete this user? This action cannot be undone.'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.authService.deleteUser(userId).subscribe({
          next: () => {
            this.toastr.success('User deleted successfully', 'Success');
            this.loadUsers();
          },
          error: (err) => {
            this.toastr.error('Failed to delete user', 'Error');
            console.error('Error deleting user:', err);
          }
        });
      }
    });
  }

  toggleUserStatus(user: any): void {
    /*const newStatus = !user.active;
    this.authService.updateUserStatus(user.id, newStatus).subscribe({
      next: () => {
        user.active = newStatus;
        this.toastr.success(`User ${newStatus ? 'activated' : 'deactivated'}`, 'Success');
      },
      error: (err:any) => {
        this.toastr.error('Failed to update user status', 'Error');
        console.error('Error updating user status:', err);
        user.active = !newStatus; // Revert the toggle
      }
    });*/
  }
}
