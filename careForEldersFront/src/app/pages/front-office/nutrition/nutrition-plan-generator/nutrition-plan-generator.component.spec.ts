import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NutritionPlanGeneratorComponent } from './nutrition-plan-generator.component';

describe('NutritionPlanGeneratorComponent', () => {
  let component: NutritionPlanGeneratorComponent;
  let fixture: ComponentFixture<NutritionPlanGeneratorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NutritionPlanGeneratorComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(NutritionPlanGeneratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
