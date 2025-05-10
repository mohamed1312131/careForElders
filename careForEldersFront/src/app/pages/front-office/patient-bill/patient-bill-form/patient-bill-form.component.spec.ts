import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientBillFormComponent } from './patient-bill-form.component';

describe('PatientBillFormComponent', () => {
  let component: PatientBillFormComponent;
  let fixture: ComponentFixture<PatientBillFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientBillFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PatientBillFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
