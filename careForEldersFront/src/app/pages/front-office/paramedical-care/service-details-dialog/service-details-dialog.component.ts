import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CareService } from '../CareService';

@Component({
  selector: 'app-service-details-dialog',
  templateUrl: './service-details-dialog.component.html',
  styleUrl: './service-details-dialog.component.scss'
})
export class ServiceDetailsDialog {
  serviceDetails: any;

  constructor(
    public dialogRef: MatDialogRef<ServiceDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private careService: CareService
  ) {
    this.loadServiceDetails(data.serviceId);
  }

  private loadServiceDetails(serviceId: string) {
    this.careService.getServiceDetails(serviceId).subscribe(details => {
      this.serviceDetails = details;
    });
  }
}