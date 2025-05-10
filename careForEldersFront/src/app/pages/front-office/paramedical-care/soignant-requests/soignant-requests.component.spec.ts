import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SoignantRequestsComponent } from './soignant-requests.component';

describe('SoignantRequestsComponent', () => {
  let component: SoignantRequestsComponent;
  let fixture: ComponentFixture<SoignantRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SoignantRequestsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SoignantRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
