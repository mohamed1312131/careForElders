import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicalRecordListComponent } from './medical-records-list.component';

describe('MedicalRecordsListComponent', () => {
  let component: MedicalRecordListComponent;
  let fixture: ComponentFixture<MedicalRecordListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MedicalRecordListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MedicalRecordListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
