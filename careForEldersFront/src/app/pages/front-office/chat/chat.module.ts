import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatRoutingModule } from './chat-routing.module';
import { ChatAIComponent } from './chat-ai/chat-ai.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialModule } from 'src/app/material.module';
import { TestComponent } from './test/test.component';
import { WebSocketService } from './WebSocketService';

// Add WebSocket service provider


@NgModule({
  declarations: [
    ChatAIComponent,
    TestComponent
  ],
  imports: [
    CommonModule,
    ChatRoutingModule,
    MaterialModule,
    HttpClientModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    WebSocketService // Add this line
  ],
  exports: [ChatAIComponent]
})
export class ChatModule { }