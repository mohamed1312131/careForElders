import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Icons
import { TablerIconsModule } from 'angular-tabler-icons';
import * as TablerIcons from 'angular-tabler-icons/icons';

// Routing
import { AuthenticationRoutes } from './authentication.routing';

// Components
import { AppSideLoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";

@NgModule({
  declarations: [
    AppSideLoginComponent,
    RegisterComponent // ✅ correctly declared
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(AuthenticationRoutes),
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    TablerIconsModule.pick(TablerIcons),
    MatOption,
    MatSelect,
    // ✅ keep icons module
  ]
})
export class AuthenticationModule {}
