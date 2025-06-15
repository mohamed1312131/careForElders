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
interface BloodType {
  value: string;
  viewValue: string;
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
  doctors: any[] = [];
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
  newMedication: Medication = { name: '', dosage: '' } ;
  notesSummary: string | null = null;
  summaryGeneratedAt: Date | null = null;
  isGeneratingSummary = false;
  currentUploadFile: string = '';
  bloodTypes: BloodType[] = [
    {value: 'A+', viewValue: 'A+'},
    {value: 'A-', viewValue: 'A-'},
    {value: 'B+', viewValue: 'B+'},
    {value: 'B-', viewValue: 'B-'},
    {value: 'AB+', viewValue: 'AB+'},
    {value: 'AB-', viewValue: 'AB-'},
    {value: 'O+', viewValue: 'O+'},
    {value: 'O-', viewValue: 'O-'}
  ];
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
    this.loadDoctors();

  }
  loadUserData(): void {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      this.toastr.error('No user found in localStorage');
      return;
    }

    try {
      const user = JSON.parse(userStr);
      this.userId = user.id;

      this.medicalService.getMedicalRecord(this.userId).subscribe({
        next: (record) => {
          if (record) {
            this.setPatientData(user, record);
          } else {
            // No record found, create a new one
            const newRecord = { userId: this.userId, bloodType: '', allergies: '', currentMedications: '', chronicConditions: '', primaryCarePhysician: '', lastPhysicalExam: null };
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
        },
        error: (err) => {
          console.error('Error fetching medical record:', err);
          this.toastr.error('Failed to load medical record');
        }
      });
    } catch (e) {
      console.error('Failed to parse user from localStorage', e);
      this.toastr.error('Failed to load user data');
    }
  }

  private setPatientData(user: any, record: any): void {
    // Convert medication strings back to objects (if needed)
    const medications = Array.isArray(record.currentMedications)
      ? record.currentMedications.map((med: string) => {
        const [name, dosage] = med.split(' - ');
        return { name, dosage };
      })
      : [];

    this.patient = {
      ...record,
      name: `${user.firstName} ${user.lastName}`,
      avatar: user.avatar || 'https://i.pravatar.cc/100?img=12',
      info: record?.chronicConditions || 'General health info',
    };

    this.medicalRecord = {
      bloodType: record.bloodType || '',
      allergies: Array.isArray(record.allergies) ? record.allergies : [record.allergies].filter(Boolean),
      currentMedications: medications, // Now stored as objects in the UI
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

    // Convert medication objects to strings (e.g., "Paracetamol - 500mg")
    const medicationsAsStrings = this.medicalRecord.currentMedications.map(
      (med: Medication) => `${med.name} - ${med.dosage}`
    );

    const updatedRecord = {
      ...this.medicalRecord,
      currentMedications: medicationsAsStrings, // Send as strings
      userId: this.userId
    };

    this.medicalService.updateMedicalRecord(this.patient.id, updatedRecord).subscribe({
      next: (updated) => {
        this.toastr.success('Medical record updated');
        this.loadUserData(); // Reload data to sync with backend
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
    if (this.newMedication.name.trim()) {
      this.medicalRecord.currentMedications.push({
        name: this.newMedication.name.trim(),
        dosage: this.newMedication.dosage.trim()
      });
      this.newMedication = { name: '', dosage: '' }; // Reset the form
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
        // Auto-generate summary when notes load
        this.generateSummary();
      },
      error: (err) => {
        this.toastr.error('Failed to load medical records');
        this.isLoading = false;
        console.error(err);
      }
    })}

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

  generateSummary(): void {
    if (this.records.length === 0) {
      this.toastr.warning('No notes available to summarize');
      return;
    }

    this.isGeneratingSummary = true;

    // Combine all notes into one text
    const allNotes = this.records.map(n => n.content).join('\n\n');

    this.medicalService.generateNotesSummary(allNotes).subscribe({
      next: (summary: string | null) => {
        this.notesSummary = summary;
        this.summaryGeneratedAt = new Date();
        this.isGeneratingSummary = false;
      },
      error: (err: any) => {
        console.error('Failed to generate summary', err);
        this.toastr.error('Failed to generate summary');
        this.isGeneratingSummary = false;
      }
    });
  }

  saveSummaryToNotes(): void {
    if (!this.notesSummary) return;

    const summaryNote = {
      userId: this.userId,
      authorId: 'ai-system',
      authorName: 'AI Assistant',
      content: `AI GENERATED SUMMARY:\n${this.notesSummary}`,
      createdAt: new Date()
    };

    this.medicalService.addMedicalNote(summaryNote).subscribe({
      next: () => {
        this.toastr.success('Summary saved to notes');
        this.loadMedicalNotes();
      },
      error: (err: any) => {
        this.toastr.error('Failed to save summary');
        console.error(err);
      }
    });
  }
  downloadDocument(doc: MedicalDocument): void {
    const url = this.getDocumentUrl(doc);
    const a = document.createElement('a');
    a.href = url as string;
    a.download = doc.filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
  editNote(noteId: string): void {
    // Implement your edit note logic here
    console.log('Editing note:', noteId);
  }


  // Add to your component class
  printRecord(): void {
    // Store original styles
    const originalStyles = document.querySelectorAll('style, link[rel="stylesheet"]');

    // Disable all styles for printing
    originalStyles.forEach(style => {
      style.setAttribute('media', 'none');
    });

    // Create print window
    const printWindow = window.open('', '_blank');
    if (!printWindow) {
      this.toastr.error('Popup blocker may be preventing the print window from opening');
      return;
    }

    // Get the content to print
    const content = document.querySelector('.medical-record-container')?.outerHTML;

    // Write the content to the print window
    printWindow.document.write(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>Medical Record - ${this.patient.name}</title>
      <style>
        body { font-family: Arial, sans-serif; color: #333; }
        .patient-header { background: linear-gradient(135deg, #6b73ff 0%, #000dff 100%); color: white; padding: 20px; border-radius: 10px; }
        .medical-section { page-break-inside: avoid; margin-bottom: 20px; border: 1px solid #eee; padding: 15px; border-radius: 8px; }
        .mat-tab-group, .mat-tab-header { display: none !important; }
        .tab-content { display: block !important; }
        @page { size: auto; margin: 10mm; }
        @media print {
          body { zoom: 90%; }
          button { display: none !important; }
          .no-print { display: none !important; }
        }
      </style>
    </head>
    <body>
      <h1 style="text-align: center; margin-bottom: 20px;">Medical Record</h1>
      ${content}
      <script>
        window.onload = function() {
          setTimeout(function() {
            window.print();
            window.close();
          }, 300);
        }
      </script>
    </body>
    </html>
  `);

    // Close the document for writing
    printWindow.document.close();

    // Restore original styles after a delay
    setTimeout(() => {
      originalStyles.forEach(style => {
        style.removeAttribute('media');
      });
    }, 1000);
  }
  loadDoctors(): void {
    this.medicalService.getDoctors().subscribe({
      next: (res) => this.doctors = res,
      error: (err) => console.error('Failed to load doctors', err)
    });
  }
}
