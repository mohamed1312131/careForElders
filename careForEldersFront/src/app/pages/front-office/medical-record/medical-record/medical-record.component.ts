import { Component, OnInit, ViewChild } from '@angular/core';
import { MedicalRecordService } from '../../../../services/medical-record.service';
import { MatPaginator } from "@angular/material/paginator";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { AuthService } from '../../../../services/auth.service';
import {AddNoteDialogComponent} from "../add-note-dialog/add-note-dialog.component";
import {DocumentPreviewDialogComponent} from "../document-preview-dialog/document-preview-dialog.component";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";

//import { AddNoteDialogComponent } from '../add-note-dialog/add-note-dialog.component';
export interface MedicalDocument {
  _id: string;
  userId: string;
  filename: string;
  fileType: string;
  fileSize: number;
  data:string;
  uploadDate: Date;
}
interface Medication {
  name: string;
  dosage: string;
}

@Component({
  selector: 'app-medical-record',
  templateUrl: './medical-record.component.html',
  styleUrls: ['./medical-record.component.scss']
})
export class MedicalRecordComponent implements OnInit {
  records: any[] = [];
  appointments: any[] = [];
  userId: string = '';
  isLoading = false;
  isUploading = false;
  // For pagination
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  uploadProgress: number | null = null;
  attachments: MedicalDocument[] = [];
  medicalRecord: any = {
    bloodType: '',
    allergies: [],
    currentMedications: [],
    chronicConditions: [],
    primaryCarePhysician: '',
    lastPhysicalExam: null
  };
  patient: any = {
    name: '',
    avatar: '',
    info: '',
    bloodType: '',
    allergies: '',
    currentMedications: '',
    chronicConditions: '',
    familyMedicalHistory: '',
    primaryCarePhysician: '',
    insuranceInformation: '',
    lastPhysicalExam: '',
    vaccinations: [],
    notes: [],
    documents: []
  };

  newAllergy = '';
  newCondition = '';
  newMedication = '';
  constructor(
    private dialog: MatDialog,
    private toastr: ToastrService,
    private authService: AuthService,
    private medicalService: MedicalRecordService,
    private activatedRoute: ActivatedRoute,
    private sanitizer: DomSanitizer
  ) {}


  ngOnInit(): void {
    this.loadUserData();
    this.loadMedicalNotes();
    this.loadAppointments();
    this.loadAttachments();
    const savedAvatar = localStorage.getItem('profileImage');
    if (savedAvatar) {
      this.patient.avatarBase64 = savedAvatar; // Load from localStorage
    }
  }
  private loadUserData(): void {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      this.toastr.error('No user found in localStorage');
      return;
    }

    try {
      const user = JSON.parse(userStr);
      this.userId = user.id;
      console.log("dzadza",user);  // Log the response to see the structure

      // Load profile image from localStorage if available
      const savedAvatar = localStorage.getItem('profileImage');
      if (savedAvatar) {
        this.patient.avatar = savedAvatar;
        console.log("azddddddddddddddddd",this.patient.avatar);
      }

      this.medicalService.getMedicalRecord(this.userId).subscribe({
        next: (record) => {
          if (record) {
            this.setPatientData(user, record);
          } else {
            this.createNewMedicalRecord(user);
          }
        },
        error: (err) => this.handleMedicalRecordError(err)
      });
    } catch (e) {
      console.error('Failed to parse user from localStorage', e);
      this.toastr.error('Failed to load user data');
    }
  }

  private createNewMedicalRecord(user: any): void {
    const newRecord = {
      userId: this.userId,
      bloodType: '',
      allergies: [],
      currentMedications: [],
      chronicConditions: [],
      primaryCarePhysician: '',
      lastPhysicalExam: null
    };

    this.medicalService.createMedicalRecord(newRecord).subscribe({
      next: (createdRecord) => {
        this.setPatientData(user, createdRecord);
        this.toastr.success('Medical record initialized');
      },
      error: (err) => {
        console.error('Failed to create medical record', err);
        this.toastr.error('Could not initialize medical record');
      }
    });
  }

  private setPatientData(user: any, record: any): void {
    this.patient = {
      ...record,
      name: `${user.firstName} ${user.lastName}`,
      avatar: user.profileImage
        ? this.sanitizer.bypassSecurityTrustUrl(`data:image/png;base64,${user.profileImage}`)
        : 'https://i.pravatar.cc/100?img=12',      info: record?.chronicConditions || 'General health info',
    };

    this.medicalRecord = {
      bloodType: record.bloodType || '',
      allergies: Array.isArray(record.allergies) ? record.allergies : [record.allergies].filter(Boolean),
      currentMedications: Array.isArray(record.currentMedications) ? record.currentMedications : [record.currentMedications].filter(Boolean),
      chronicConditions: Array.isArray(record.chronicConditions) ? record.chronicConditions : [record.chronicConditions].filter(Boolean),
      primaryCarePhysician: record.primaryCarePhysician || '',
      lastPhysicalExam: record.lastPhysicalExam || null
    };

    this.patient.id = record.id;
  }




  updateMedicalRecord(): void {
    if (!this.patient.id) {
      this.toastr.error('Medical record ID missing');
      return;
    }

    const updatedRecord = {
      ...this.medicalRecord,
      userId: this.userId
    };

    this.medicalService.updateMedicalRecord(this.patient.id, updatedRecord).subscribe({
      next: (updated) => {
        this.toastr.success('Medical record updated');
        this.setPatientData({ id: this.userId, ...this.patient }, updated);  // Update UI with latest
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Failed to update medical record');
      }
    });
  }


  addAllergy(): void {
    if (this.newAllergy.trim()) {
      this.medicalRecord.allergies.push(this.newAllergy.trim());
      this.newAllergy = '';
    }
  }

  removeAllergy(index: number): void {
    this.medicalRecord.allergies.splice(index, 1);
  }

  addCondition(): void {
    if (this.newCondition.trim()) {
      this.medicalRecord.chronicConditions.push(this.newCondition.trim());
      this.newCondition = '';
    }
  }

  removeCondition(index: number): void {
    this.medicalRecord.chronicConditions.splice(index, 1);
  }

  addMedication(): void {
    if (this.newMedication.trim()) {
      this.medicalRecord.currentMedications.push(this.newMedication.trim());
      this.newMedication = '';
    }
  }

  removeMedication(index: number): void {
    this.medicalRecord.currentMedications.splice(index, 1);
  }
  cancelChanges(): void {
    // Reload the original data
    this.loadUserData();
  }
  loadAttachments(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.medicalService.getDocumentsForUser(this.userId).subscribe({
      next: (docs: any[]) => {
        this.attachments = docs.map(doc => ({
          ...doc,
          uploadDate: new Date(doc.uploadDate)
        }));
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error('Failed to load documents');
        console.error(err);
      }
    });
  }


  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.uploadDocument(file);
    }
  }

  deleteDocument(id: string): void {
    if (confirm('Are you sure you want to delete this document?')) {
      this.medicalService.deleteDocument(id).subscribe({
        next: () => this.loadAttachments(),
        error: (err) => console.error('Delete failed', err)
      });
    }
  }

  private getDocumentUrl(document: MedicalDocument): SafeUrl {
    const base64Data = document.data.split(',')[1] || document.data;
    const byteCharacters = atob(base64Data);
    const byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], {type: document.fileType});
    return this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob));
  }


  loadMedicalNotes(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.medicalService.getMedicalNotes(this.userId).subscribe({
      next: (notes) => {
        this.records = notes;
        this.isLoading = false;
      },
      error: (err) => {
        this.toastr.error('Failed to load medical records');
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  loadAppointments(): void {
    if (!this.userId) return;

    this.isLoading = true;
    this.medicalService.getAppointments(this.userId).subscribe({
      next: (appts) => {
        this.appointments = appts;
        this.isLoading = false;
      },
      error: (err) => {
        this.toastr.error('Failed to load appointments');
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  refreshAppointments(): void {
    this.loadAppointments();
  }
  deleteMedicalNote(noteId: string): void {
    // Logic to delete the note
    this.medicalService.deleteMedicalNote(noteId).subscribe({
      next: () => {
        this.records = this.records.filter(note => note.id !== noteId);
        this.toastr.success('Note deleted');
      },
      error: (err) => {
        this.toastr.error('Failed to delete note');
        console.error(err);
      }
    });
  }
  openAddNoteDialog(): void {
    const dialogRef = this.dialog.open(AddNoteDialogComponent, {
      width: '600px',
      data: { userId: this.userId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadMedicalNotes();
      }
    });
  }
  openUploadDialog(): void {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*,.pdf,.doc,.docx';

    fileInput.onchange = (event: any) => {
      const file = event.target.files[0];
      if (file) {
        this.uploadDocument(file);
      }
    };

    fileInput.click();
  }

  uploadDocument(file: File): void {
    this.isUploading = true;
    this.uploadProgress = 0;

    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', this.userId);

    this.medicalService.uploadDocument(file, this.userId).subscribe({
      next: (event: any) => {
        if (event.type === 1 && event.loaded && event.total) {
          // Upload progress
          this.uploadProgress = Math.round(100 * event.loaded / event.total);
        } else if (event.type === 4) {
          // Upload complete
          this.uploadProgress = null;
          this.isUploading = false;
          this.toastr.success('File uploaded successfully');
          // Force reload attachments
          this.loadAttachments();
        }
      },
      error: (err) => {
        this.isUploading = false;
        this.uploadProgress = null;
        this.toastr.error('Failed to upload file');
        console.error(err);
      }
    });
  }
  viewDocument(document: MedicalDocument): void {
    const url = this.getDocumentUrl(document);
    if (document.fileType.startsWith('image/') || document.fileType.includes('pdf')) {
      this.dialog.open(DocumentPreviewDialogComponent, {
        width: '80vw',
        height: '90vh',
        data: {
          name: document.filename,
          type: document.fileType,
          src: url,
          downloadUrl: url
        },
        panelClass: 'document-preview-dialog'
      });
    } else {
      window.open(url as string, '_blank');
    }
  }
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }



  deleteAttachment(attachmentId: string): void {
    // Static implementation - just remove from local array
    this.attachments = this.attachments.filter(a => a._id !== attachmentId);
    this.toastr.success('Attachment deleted (demo)');


  }





  markAppointmentComplete(appointmentId: string): void {
    // Static implementation - just update local array
    const appointment = this.appointments.find(a => a.id === appointmentId);
    if (appointment) {
      appointment.status = 'completed';
      this.toastr.success('Appointment marked as complete (demo)');
    }
  }

  cancelAppointment(appointmentId: string): void {
    // Static implementation - just remove from local array
    this.appointments = this.appointments.filter(a => a.id !== appointmentId);
    this.toastr.success('Appointment cancelled (demo)');
  }
  isImage(type: string): boolean {
    return type.startsWith('image/');
  }

  isDocument(type: string): boolean {
    return type.includes('word') || type.includes('document');
  }
// Add these methods to your component
  getFileIcon(type: string): string {
    if (!type) return 'insert_drive_file';

    type = type.toLowerCase();
    if (type.startsWith('image/')) return 'image';
    if (type.includes('pdf')) return 'picture_as_pdf';
    if (type.includes('word') || type.includes('document')) return 'description';
    if (type.includes('excel') || type.includes('sheet')) return 'grid_on';
    if (type.includes('text') || type.includes('plain')) return 'text_snippet';

    return 'insert_drive_file';
  }

  getFileTypeLabel(type: string): string {
    if (!type) return 'FILE';

    type = type.toLowerCase();
    if (type.startsWith('image/')) return 'IMG';
    if (type.includes('pdf')) return 'PDF';
    if (type.includes('word')) return 'DOC';
    if (type.includes('excel')) return 'XLS';
    if (type.includes('text')) return 'TXT';

    return 'FILE';
  }

// Make sure your attachments data is properly formatted
  formatAttachments(docs: any[]): any[] {
    return docs.map(doc => ({
      id: doc.id,
      name: doc.name || 'Unnamed Document',
      type: doc.type || 'application/octet-stream',
      src: doc.src || this.getDefaultIcon(doc.type)
    }));
  }

  private getDefaultIcon(type: string): string {
    // Return placeholder image based on file type
    if (type.includes('pdf')) return 'assets/pdf-icon.png';
    if (type.includes('word')) return 'assets/doc-icon.png';
    if (type.startsWith('image/')) return 'assets/image-placeholder.png';
    return 'assets/file-icon.png';
  }
  getSafeUrl(url: string) {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  viewAttachment(doc: any): void {
    this.dialog.open(DocumentPreviewDialogComponent, {
      width: '80vw',
      height: '90vh',
      data: {
        name: doc.name,
        type: doc.type,
        src: doc.type.startsWith('image/') || doc.type.includes('pdf')
          ? doc.src
          : this.getFileIconUrl(doc.type),
        downloadUrl: doc.src
      },
      panelClass: 'document-preview-dialog'
    });
  }

  private getFileIconUrl(type: string): string {
    // Return appropriate icon based on file type
    if (type.includes('pdf')) return 'assets/pdf-icon-large.png';
    if (type.includes('word')) return 'assets/doc-icon-large.png';
    if (type.includes('excel')) return 'assets/xls-icon-large.png';
    return 'assets/file-icon-large.png';
  }

  private handleMedicalRecordError(err: any): void {
    console.error('Error fetching medical record:', err);
    this.toastr.error('Failed to load medical record');
  }
  getSafeAvatar(): SafeUrl {
    if (this.patient.avatar?.startsWith('data:')) {
      return this.sanitizer.bypassSecurityTrustUrl(this.patient.avatar);
    }
    return this.patient.avatar;
  }
}
