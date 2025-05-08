// chat-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChatAIComponent } from './chat-ai/chat-ai.component';


const routes: Routes = [
  {
    path: 'AI',  // Chat specific route
    component: ChatAIComponent
  },
  
  { path: '', redirectTo: 'test', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ChatRoutingModule { }