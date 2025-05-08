import { TestBed } from '@angular/core/testing';
import { PatientBillService } from './patient-bill.service';

describe('PatientBillServiceService', () => {
  let service: PatientBillService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PatientBillService);
  });

  it('should be created', () => { 
    expect(service).toBeTruthy();
  });
});
