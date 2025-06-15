import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CareService } from '../CareService';

@Component({
  selector: 'app-soignant-requests',
  templateUrl: './soignant-requests.component.html',
  styleUrl: './soignant-requests.component.scss'
})
export class SoignantRequestsComponent {
  displayedColumns: string[] = ['id', 'serviceName', 'requestedAt', 'user', 'status', 'actions'];
  dataSource: MatTableDataSource<any>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private careService: CareService) {
    this.dataSource = new MatTableDataSource<any>();
    this.loadRequests();
  }

  private loadRequests() {
    this.careService.getPendingRequests().subscribe(requests => {
      this.dataSource.data = requests;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  assignRequest(requestId: string) {
    const soignantId = localStorage.getItem('user_id'); // Get from auth service
    this.careService.assignRequest(requestId, soignantId!).subscribe(() => {
      this.loadRequests();
    });
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}