import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { MedicalRecordService } from '../../../../services/medical-record.service';

@Component({
  selector: 'app-add-note-dialog',
  templateUrl: './add-note-dialog.component.html',
  styleUrls: ['./add-note-dialog.component.scss']
})
export class AddNoteDialogComponent {
  noteForm: FormGroup;
  isLoading = false;

  constructor(
    public dialogRef: MatDialogRef<AddNoteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { userId: string },
    private fb: FormBuilder,
    private medicalService: MedicalRecordService,
    private toastr: ToastrService
  ) {
    this.noteForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  onSubmit(): void {
    if (this.noteForm.valid) {
      this.isLoading = true;

      const noteData = {
        userId: this.data.userId,
        content: this.noteForm.value.content,
        authorId: 'current-user-id', // Replace with actual user ID from auth
        authorName: 'Current User'   // Replace with actual user name
      };

      // Call the MedicalRecordService to actually add the note to the backend
      this.medicalService.addMedicalNote(noteData).subscribe({
        next: () => {
          this.toastr.success('Note added successfully');
          this.isLoading = false;
          this.dialogRef.close(true); // Close the dialog on success
        },
        error: (err) => {
          this.toastr.error('Failed to add note');
          console.error(err);
          this.isLoading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
