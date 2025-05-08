import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientBillListComponent } from './patient-bill-list.component';

describe('PatientBillListComponent', () => {
  let component: PatientBillListComponent;
  let fixture: ComponentFixture<PatientBillListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientBillListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PatientBillListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
