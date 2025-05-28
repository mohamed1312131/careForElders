import { Component, ElementRef, Inject, OnDestroy, AfterViewInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

declare var JitsiMeetExternalAPI: any;

@Component({
  selector: 'app-jitsi-dialog',
  templateUrl: './jitsi-dialog.component.html',
  styleUrls: ['./jitsi-dialog.component.scss']
})
export class JitsiDialogComponent implements AfterViewInit, OnDestroy {
  @ViewChild('jitsiContainer', { static: false }) jitsiContainer!: ElementRef;
  domain: string = 'meet.jit.si';
  api: any;

  constructor(
    public dialogRef: MatDialogRef<JitsiDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { roomName: string }
  ) {}

ngAfterViewInit(): void {
    const options = {
      roomName: this.data.roomName,
      width: '100%',
      height: 500,
      parentNode: this.jitsiContainer.nativeElement,
      interfaceConfigOverwrite: {
        SHOW_JITSI_WATERMARK: false,
      },
      userInfo: {
        displayName: this.data.roomName || 'Participant' // Safe fallback
      }
    };
    this.api = new JitsiMeetExternalAPI(this.domain, options);
  }

  ngOnDestroy(): void {
    if (this.api) {
      this.api.dispose();
    }
  }
}
