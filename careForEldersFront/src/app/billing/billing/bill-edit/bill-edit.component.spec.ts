import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillEditComponent } from './bill-edit.component';

describe('BillEditComponent', () => {
  let component: BillEditComponent;
  let fixture: ComponentFixture<BillEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BillEditComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BillEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
