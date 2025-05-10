import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientBillPaymentComponent } from './patient-bill-payment.component';

describe('PatientBillPaymentComponent', () => {
  let component: PatientBillPaymentComponent;
  let fixture: ComponentFixture<PatientBillPaymentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientBillPaymentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PatientBillPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
