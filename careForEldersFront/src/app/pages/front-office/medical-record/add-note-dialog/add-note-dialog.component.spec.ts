import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNoteDialogComponent } from './add-note-dialog.component';

describe('AddNoteDialogComponent', () => {
  let component: AddNoteDialogComponent;
  let fixture: ComponentFixture<AddNoteDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddNoteDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddNoteDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
