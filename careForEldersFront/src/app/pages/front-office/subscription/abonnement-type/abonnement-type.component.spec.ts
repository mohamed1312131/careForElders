import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AbonnementTypeComponent } from './abonnement-type.component';

describe('AbonnementTypeComponent', () => {
  let component: AbonnementTypeComponent;
  let fixture: ComponentFixture<AbonnementTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AbonnementTypeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AbonnementTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
