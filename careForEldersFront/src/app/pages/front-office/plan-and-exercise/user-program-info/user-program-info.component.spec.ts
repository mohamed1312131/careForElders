import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProgramInfoComponent } from './user-program-info.component';

describe('UserProgramInfoComponent', () => {
  let component: UserProgramInfoComponent;
  let fixture: ComponentFixture<UserProgramInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserProgramInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserProgramInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
