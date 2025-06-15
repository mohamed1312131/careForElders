import { Component, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CareService } from '../CareService';
import { ServiceDetailsDialog } from '../service-details-dialog/service-details-dialog.component';

@Component({
  selector: 'app-doctor-services',
  templateUrl: './doctor-services.component.html',
  styleUrl: './doctor-services.component.scss'
})
export class DoctorServicesComponent {
  displayedColumns: string[] = ['name', 'category', 'price', 'active', 'requests', 'actions'];
  dataSource: MatTableDataSource<any>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private careService: CareService, public dialog: MatDialog) {
    this.dataSource = new MatTableDataSource<any>();
    this.loadServices();
  }

  private loadServices() {
    const doctorId = localStorage.getItem('userId');
    if (doctorId) {
      this.careService.getDoctorServices(doctorId).subscribe({
        next: (services) => {
          this.dataSource.data = services;
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        },
        error: (err) => console.error('Error loading services:', err)
      });
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  showDetails(service: any) {
    this.dialog.open(ServiceDetailsDialog, {
      data: { serviceId: service.id },
      width: '600px'
    });
  }
}