import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DoctorAddProgramComponent } from './doctor-add-program.component';

describe('DoctorAddProgramComponent', () => {
  let component: DoctorAddProgramComponent;
  let fixture: ComponentFixture<DoctorAddProgramComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoctorAddProgramComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DoctorAddProgramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
