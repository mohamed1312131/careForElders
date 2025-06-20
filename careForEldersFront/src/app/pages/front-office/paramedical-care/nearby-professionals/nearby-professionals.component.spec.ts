import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NearbyProfessionalsComponent } from './nearby-professionals.component';

describe('NearbyProfessionalsComponent', () => {
  let component: NearbyProfessionalsComponent;
  let fixture: ComponentFixture<NearbyProfessionalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NearbyProfessionalsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(NearbyProfessionalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
