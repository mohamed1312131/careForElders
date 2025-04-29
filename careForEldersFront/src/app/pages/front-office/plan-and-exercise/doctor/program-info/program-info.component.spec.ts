import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgramInfoComponent } from './program-info.component';

describe('ProgramInfoComponent', () => {
  let component: ProgramInfoComponent;
  let fixture: ComponentFixture<ProgramInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgramInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProgramInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
