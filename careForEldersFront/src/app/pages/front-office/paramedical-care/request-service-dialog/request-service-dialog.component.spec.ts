import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestServiceDialogComponent } from './request-service-dialog.component';

describe('RequestServiceDialogComponent', () => {
  let component: RequestServiceDialogComponent;
  let fixture: ComponentFixture<RequestServiceDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestServiceDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RequestServiceDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
