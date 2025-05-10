import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MedicalRecordRoutingModule } from './medical-record-routing.module';
import {MedicalRecordComponent} from "./medical-record/medical-record.component";
import {MedicalRecordListComponent} from "./medical-records-list/medical-records-list.component";
import {DocumentPreviewDialogComponent} from "./document-preview-dialog/document-preview-dialog.component";
import {AddNoteDialogComponent} from "./add-note-dialog/add-note-dialog.component";


@NgModule({
  declarations: [
    DocumentPreviewDialogComponent,
    AddNoteDialogComponent
  ],
  imports: [
    CommonModule,
    MedicalRecordRoutingModule,
    MedicalRecordListComponent
  ],
  exports:[
    MedicalRecordListComponent,
    DocumentPreviewDialogComponent,
    AddNoteDialogComponent
  ]
})
export class MedicalRecordModule { }
