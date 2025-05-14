import { Component, OnInit, ViewChild } from '@angular/core';
import { MedicalRecordService } from '../../../../services/medical-record.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule } from '@angular/material/paginator';

interface MedicalRecord {
  id: string;
  userId: string;
  bloodType: string;
  allergies: string[];
  currentMedications: string[];
  chronicConditions: string[];
  lastPhysicalExam: string;
  updatedAt?: string; // Added optional field to match your template

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
    SlicePipe,
    DatePipe
  ],
  styleUrls: ['./medical-records-list.component.scss']
})
export class MedicalRecordListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'patientId', 'bloodType', 'lastPhysicalExam', 'actions'];
  dataSource = new MatTableDataSource<MedicalRecord>();
  isLoading = false;
  records = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private medicalService: MedicalRecordService,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadMedicalRecords();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  loadMedicalRecords(): void {
    this.isLoading = true;
    this.medicalService.getAllMedicalRecords().subscribe({
      next: (records: import('../../../../services/medical-record.service').MedicalRecord[]) => {
        // Add current date as updatedAt if not present
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

  viewDetails(id: string): void {
    if (!id) {
      console.error('Invalid record ID');
      return;
    }
    this.router.navigate(['/user/userProfile/medicalRecord/medicalrecord/', id]);
  }

  viewPatientDetails(patientId: string): void {
    if (!patientId) {
      console.error('Invalid patient ID');
      return;
    }
    this.router.navigate(['/patients', patientId]);
  }

  refreshRecords(): void {
    this.loadMedicalRecords();
  }
  goToDetails(id: number) {
    this.router.navigate(['/user/userProfile/medicalRecord/medicalrecord/', id]);
  }

}
