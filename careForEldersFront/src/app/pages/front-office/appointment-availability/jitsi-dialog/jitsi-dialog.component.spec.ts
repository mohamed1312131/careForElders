import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JitsiDialogComponent } from './jitsi-dialog.component';

describe('JitsiDialogComponent', () => {
  let component: JitsiDialogComponent;
  let fixture: ComponentFixture<JitsiDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JitsiDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(JitsiDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
