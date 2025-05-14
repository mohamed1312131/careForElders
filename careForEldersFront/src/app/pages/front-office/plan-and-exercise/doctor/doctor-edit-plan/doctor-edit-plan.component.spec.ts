import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DoctorEditPlanComponent } from './doctor-edit-plan.component';

describe('DoctorEditPlanComponent', () => {
  let component: DoctorEditPlanComponent;
  let fixture: ComponentFixture<DoctorEditPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoctorEditPlanComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DoctorEditPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
