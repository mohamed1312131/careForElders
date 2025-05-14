import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientBillHistoryComponent } from './patient-bill-history.component';

describe('PatientBillHistoryComponent', () => {
  let component: PatientBillHistoryComponent;
  let fixture: ComponentFixture<PatientBillHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientBillHistoryComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PatientBillHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
