import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChatAIComponent } from './chat-ai/chat-ai.component';
import { TestComponent } from './test/test.component';

const routes: Routes = [
  {
      path: 'chat/:userId',
      component: ChatAIComponent,
    },  {
      path: 'test',
      component: TestComponent,
    },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ChatRoutingModule { }
