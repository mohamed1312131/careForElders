import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParamedicalMapComponent } from './paramedical-map.component';

describe('ParamedicalMapComponent', () => {
  let component: ParamedicalMapComponent;
  let fixture: ComponentFixture<ParamedicalMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParamedicalMapComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ParamedicalMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
