import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Component({
  selector: 'app-document-preview-dialog',
  templateUrl: './document-preview-dialog.component.html',
  styleUrls: ['./document-preview-dialog.component.scss']
})
export class DocumentPreviewDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DocumentPreviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  private sanitizer: DomSanitizer
  ) {}

  closeDialog(): void {
    this.dialogRef.close();
  }

  isImage(type: string): boolean {
    return type.startsWith('image/');
  }

  isPDF(type: string): boolean {
    return type.includes('pdf');
  }

  isViewable(type: string): boolean {
    return this.isImage(type) || this.isPDF(type);
  }
  downloadDocument(): void {
    const link = document.createElement('a');
    link.href = this.data.downloadUrl;
    link.download = this.data.name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
  getSafeUrl(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
}
