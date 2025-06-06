import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Angular Material Modules
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatError } from '@angular/material/form-field';

// Icons
import { TablerIconsModule } from 'angular-tabler-icons';
import * as TablerIcons from 'angular-tabler-icons/icons';

// Routing
import { AuthenticationRoutes } from './authentication.routing';

// Components
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { PasswordResetComponent } from './password-reset/password-reset.component';
import { ResetPasswordFormComponent } from './reset-password-form/reset-password-form.component';
import {AuthService} from "../../services/auth.service";
import {VerifyOtpComponent} from "./verify-otp/verify-otp.component";
import {OtpModalComponent} from "./otp-modal/otp-modal.component";
import {MatDialogModule} from "@angular/material/dialog";
import {ChooseMethodModalComponent} from "./choose-method-modal/choose-method-modal.component";
import {OAuth2RedirectComponent} from "./OAuth2Redirect/OAuth2RedirectComponent";

@NgModule({
  declarations: [
    RegisterComponent,
    LoginComponent,
    PasswordResetComponent,
    ResetPasswordFormComponent,
    VerifyOtpComponent,
    OtpModalComponent,
    ChooseMethodModalComponent,
    OAuth2RedirectComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(AuthenticationRoutes),
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,

    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatError,

    // Icons
    TablerIconsModule.pick(TablerIcons)
  ],providers:[AuthService]
})
export class AuthenticationModule {}
