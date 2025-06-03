import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-choose-method-modal',
  templateUrl: './choose-method-modal.component.html',
  styleUrls: ['./choose-method-modal.component.scss']
})
export class ChooseMethodModalComponent {
  constructor(
    public dialogRef: MatDialogRef<ChooseMethodModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  choose(method: 'sms' | 'email'): void {
    this.dialogRef.close(method);
  }

  cancel(): void {
    this.dialogRef.close(null);
  }
}
