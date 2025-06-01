import { Component, OnInit, ViewChild } from '@angular/core';
import { MedicalRecordService } from '../../../../services/medical-record.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {UserService} from "../../../../services/user.service";
import {map} from "rxjs/operators";

interface MedicalRecord {
  id: string;
  userId: string;
  bloodType: string;
  allergies: string[];
  currentMedications: string[];
  chronicConditions: string[];
  lastPhysicalExam: string;
  updatedAt?: string;
}

@Component({
  selector: 'app-medical-record-list',
  templateUrl: './medical-records-list.component.html',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatIconModule,
    MatProgressBarModule,
    MatButtonModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    SlicePipe
  ],
  providers: [DatePipe],
  styleUrls: ['./medical-records-list.component.scss']
})
export class MedicalRecordListComponent implements OnInit {
  displayedColumns: string[] = ['Patient', 'bloodType', 'lastPhysicalExam', 'actions'];
  dataSource = new MatTableDataSource<MedicalRecord>();
  isLoading = false;
  searchTerm = '';
  userNameMap: { [userId: string]: string } = {};

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private medicalService: MedicalRecordService,
    private userservice : UserService,
    private router: Router,
    private dialog: MatDialog,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.loadMedicalRecords();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadMedicalRecords(): void {
    this.isLoading = true;
    this.medicalService.getAllMedicalRecords().subscribe({
      next: (records) => {
        // @ts-ignore
        this.dataSource.data = records.map(record => ({
          ...record,
          updatedAt: record.lastPhysicalExam || new Date().toISOString()
        }));
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading records:', err);
        this.isLoading = false;
      }
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.searchTerm = filterValue;
    this.dataSource.filterPredicate = (data: MedicalRecord, filter: string): boolean => {
      return (
        data.id.toLowerCase().includes(filter) ||
        data.userId.toLowerCase().includes(filter) ||
        (data.bloodType?.toLowerCase() || '').includes(filter)
      );
    };
    this.dataSource.filter = filterValue;
  }

  exportToCSV(): void {
    const rows = this.dataSource.filteredData;
    const csv = [
      ['Record ID', 'Patient ID', 'Blood Type', 'Last Physical Exam'],
      ...rows.map(r => [
        r.id,
        r.userId,
        r.bloodType,
        this.datePipe.transform(r.lastPhysicalExam, 'mediumDate') || ''
      ])
    ].map(e => e.join(',')).join('\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.setAttribute('download', 'medical_records.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  viewDetails(id: string): void {
    if (!id) return;
    console.log("aaaaaa",id);
    this.router.navigate(['/user/userProfile/medicalRecord/medicalrecord', id]);
  }

  getUserFullName(userId: string): string {
    if (this.userNameMap[userId]) {
      return this.userNameMap[userId];
    }

    this.userservice.getUserById(userId).subscribe({
      next: (user) => {
        this.userNameMap[userId] = `${user.firstName} ${user.lastName}`;
      },
      error: () => {
        this.userNameMap[userId] = 'Unknown';
      }
    });

    return '...'; // Placeholder while fetching
  }

  refreshRecords(): void {
    this.searchTerm = '';
    this.dataSource.filter = '';
    this.loadMedicalRecords();
  }
}
