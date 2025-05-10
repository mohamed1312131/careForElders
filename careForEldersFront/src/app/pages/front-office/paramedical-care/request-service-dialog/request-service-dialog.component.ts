import { Component, Inject } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-request-service-dialog',
  templateUrl: './request-service-dialog.component.html',
  styleUrl: './request-service-dialog.component.scss'
})
export class RequestServiceDialog {
  instructionsControl = new FormControl('');
  durationControl = new FormControl('', [Validators.required, Validators.min(1)]);

  constructor(
    public dialogRef: MatDialogRef<RequestServiceDialog>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  formValid(): boolean {
    return this.durationControl.valid;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    this.dialogRef.close({
      serviceId: this.data.service.id,
      specialInstructions: this.instructionsControl.value,
      requiredDurationHours: this.durationControl.value
    });
  }
}