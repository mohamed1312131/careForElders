import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingStepBlockComponent } from './loading-step-block.component';

describe('LoadingStepBlockComponent', () => {
  let component: LoadingStepBlockComponent;
  let fixture: ComponentFixture<LoadingStepBlockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingStepBlockComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LoadingStepBlockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
