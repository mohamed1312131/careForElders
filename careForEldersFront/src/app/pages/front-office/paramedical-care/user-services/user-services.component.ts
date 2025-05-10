import { Component, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CareService } from '../CareService';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RequestServiceDialog } from '../request-service-dialog/request-service-dialog.component';

@Component({
  selector: 'app-user-services',
  templateUrl: './user-services.component.html',
  styleUrl: './user-services.component.scss'
})

export class UserServicesComponent {
  displayedColumns: string[] = ['name', 'category', 'price', 'description', 'actions'];
  dataSource: MatTableDataSource<any>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private careService: CareService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
    this.dataSource = new MatTableDataSource<any>();
    this.loadServices();
  }

  private loadServices() {
    this.careService.getActiveServices().subscribe({
      next: (services) => {
        this.dataSource.data = services;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (err) => console.error('Error loading services:', err)
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  requestService(service: any) {
    const dialogRef = this.dialog.open(RequestServiceDialog, {
      data: { service },
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const requestData = {
          userId: localStorage.getItem('user_id'),
          serviceId: result.serviceId,
          specialInstructions: result.specialInstructions,
          requiredDurationHours: result.requiredDurationHours
        };

        this.careService.createServiceRequest(requestData).subscribe({
          next: () => {
            this.snackBar.open('Service requested successfully!', 'Close', {
              duration: 3000
            });
          },
          error: (err) => {
            this.snackBar.open('Error requesting service', 'Close', {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }
    });
  }
}