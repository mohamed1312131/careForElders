// chat-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChatAIComponent } from './chat-ai/chat-ai.component';
import { TestComponent } from './test/test.component';
import { DealsComponent } from './deals/deals.component';
import { CreateDealComponent } from './create-deal/create-deal.component';
import { PipelineComponent } from './pipeline/pipeline.component';
import { ActivitiesComponent } from './activities/activities.component';

const routes: Routes = [
  {
    path: 'AI:userId',  // Chat specific route
    component: ChatAIComponent
  },
  {
    path: 'test',
    component: TestComponent,
    children: [
      { path: 'deals', component: DealsComponent },
      { path: 'deals/create', component: CreateDealComponent },
      { path: 'deals/pipeline', component: PipelineComponent },
      { path: 'activities', component: ActivitiesComponent },
      { path: '', redirectTo: 'deals', pathMatch: 'full' }
    ]
  },
  { path: '', redirectTo: 'test', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ChatRoutingModule { }