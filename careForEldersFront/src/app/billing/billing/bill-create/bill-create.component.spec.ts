import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillCreateComponent } from './bill-create.component';

describe('BillCreateComponent', () => {
  let component: BillCreateComponent;
  let fixture: ComponentFixture<BillCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BillCreateComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BillCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
