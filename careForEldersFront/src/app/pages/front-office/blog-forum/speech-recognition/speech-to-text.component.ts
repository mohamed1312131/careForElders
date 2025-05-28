// speech-to-text.component.ts
import { Component, OnDestroy } from '@angular/core';
import { SpeechRecognitionService } from './speech-recognition.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-speech-to-text',
  template: `
    <button (click)="toggleListening()">
      {{ isListening ? 'Stop' : 'Start' }} Listening
    </button>
    <div>
      <p>Transcript: {{ transcript }}</p>
      <p *ngIf="isProcessing">Processing final transcript...</p>
    </div>
  `,
})
export class SpeechToTextComponent implements OnDestroy {
  isListening = false;
  isProcessing = false;
  transcript = '';
  private subscription: Subscription | null = null;

  constructor(private speechService: SpeechRecognitionService) {}

  async toggleListening() {
    if (this.isListening) {
      this.stopListening();
    } else {
      await this.startListening();
    }
  }

  private async startListening() {
    this.isListening = true;
    this.transcript = '';
    this.subscription = this.speechService.getTranscript().subscribe({
      next: (text) => this.transcript = text,
      error: (err) => console.error(err)
    });
    
    try {
      await this.speechService.startListening();
    } catch (error) {
      console.error('Error starting recognition:', error);
      this.isListening = false;
    }
  }

  private stopListening() {
    this.isListening = false;
    this.isProcessing = true;
    this.speechService.stopListening();
    this.isProcessing = false;
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.isListening) {
      this.speechService.stopListening();
    }
  }
}