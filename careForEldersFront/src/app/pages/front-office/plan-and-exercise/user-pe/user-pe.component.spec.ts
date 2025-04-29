import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPEComponent } from './user-pe.component';

describe('UserPEComponent', () => {
  let component: UserPEComponent;
  let fixture: ComponentFixture<UserPEComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserPEComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserPEComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
