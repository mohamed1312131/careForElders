import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DoctorPlanListComponent } from './doctor-plan-list.component';

describe('DoctorPlanListComponent', () => {
  let component: DoctorPlanListComponent;
  let fixture: ComponentFixture<DoctorPlanListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoctorPlanListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DoctorPlanListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
