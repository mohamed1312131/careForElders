import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CareService } from '../CareService';

@Component({
  selector: 'app-user-requests',
  templateUrl: './user-requests.component.html',
  styleUrl: './user-requests.component.scss'
})
export class UserRequestsComponent {
displayedColumns: string[] = ['serviceName', 'status', 'requestedAt', 'soignant', 'duration'];
  dataSource: MatTableDataSource<any>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private careService: CareService) {
    this.dataSource = new MatTableDataSource<any>();
    this.loadRequests();
  }

  private loadRequests() {
    const userId = localStorage.getItem('user_id'); // Get from auth service
    this.careService.getUserRequests(userId!).subscribe(requests => {
      this.dataSource.data = requests;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  getStatusColor(status: string): string {
    switch(status) {
      case 'PENDING': return 'orange';
      case 'IN_PROGRESS': return 'blue';
      case 'COMPLETED': return 'green';
      default: return 'black';
    }
  }
}