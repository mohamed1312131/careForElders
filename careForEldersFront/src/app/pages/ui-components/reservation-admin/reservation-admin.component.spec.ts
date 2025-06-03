import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservationAdminComponent } from './reservation-admin.component';

describe('ReservationAdminComponent', () => {
  let component: ReservationAdminComponent;
  let fixture: ComponentFixture<ReservationAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationAdminComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ReservationAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
