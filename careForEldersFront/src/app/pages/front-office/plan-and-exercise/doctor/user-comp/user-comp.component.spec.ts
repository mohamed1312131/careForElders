import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserCompComponent } from './user-comp.component';

describe('UserCompComponent', () => {
  let component: UserCompComponent;
  let fixture: ComponentFixture<UserCompComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserCompComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserCompComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
