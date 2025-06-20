import { ComponentFixture, TestBed } from '@angular/core/testing';

<<<<<<<< HEAD:careForEldersFront/src/app/pages/front-office/paramedical-care/appointment-form/appointment-form.component.spec.ts
import { AppointmentFormComponent } from './appointment-form.component';

describe('AppointmentFormComponent', () => {
  let component: AppointmentFormComponent;
  let fixture: ComponentFixture<AppointmentFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AppointmentFormComponent);
========
import { AbonnementAdminComponent } from './abonnement-admin.component';

describe('AbonnementAdminComponent', () => {
  let component: AbonnementAdminComponent;
  let fixture: ComponentFixture<AbonnementAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AbonnementAdminComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AbonnementAdminComponent);
>>>>>>>> ebe25a6 (change):careForEldersFront/src/app/pages/ui-components/abonnement-admin/abonnement-admin.component.spec.ts
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
