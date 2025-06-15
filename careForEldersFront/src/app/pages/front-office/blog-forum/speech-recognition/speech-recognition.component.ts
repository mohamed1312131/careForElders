import { Component,  OnDestroy } from "@angular/core"
import  { SpeechRecognitionService } from "./speech-recognition.service"
import  { Subscription } from "rxjs"

@Component({
  selector: "app-speech-to-text",
  template: `
    <div class="speech-container">
      <h3>Speech to Text</h3>
      
      <div *ngIf="!speechService.isSupported()" class="error">
        <p>Speech recognition is not supported in this browser.</p>
        <p>Please try using Chrome, Edge, or Safari.</p>
      </div>
      
      <div *ngIf="speechService.isSupported()">
        <div class="controls">
          <button 
            (click)="toggleListening()" 
            [class.listening]="isListening"
            [disabled]="!speechService.isSupported()"
            class="record-btn">
            <span class="btn-icon">{{ isListening ? '🛑' : '🎤' }}</span>
            {{ isListening ? 'Stop Recording' : 'Start Recording' }}
          </button>
          
          <button 
            (click)="clearTranscript()" 
            class="clear-btn"
            [disabled]="!transcript">
            Clear
          </button>
        </div>

        <div class="language-selector">
          <label for="language">Language:</label>
          <select id="language" (change)="changeLanguage($event)" value="en-US">
            <option value="en-US">English (US)</option>
            <option value="en-GB">English (UK)</option>
            <option value="es-ES">Spanish</option>
            <option value="fr-FR">French</option>
            <option value="de-DE">German</option>
            <option value="it-IT">Italian</option>
            <option value="pt-BR">Portuguese (Brazil)</option>
          </select>
        </div>
        
        <div class="transcript" *ngIf="transcript">
          <h4>Transcript:</h4>
          <p>{{ transcript }}</p>
        </div>

        <div class="status" *ngIf="isListening">
          <p class="listening-indicator">🎤 Listening... Speak now!</p>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
    .speech-container { 
      padding: 20px; 
      max-width: 600px;
      margin: 0 auto;
    }
    
    .controls {
      display: flex;
      gap: 10px;
      margin-bottom: 15px;
    }
    
    .record-btn { 
      padding: 12px 20px; 
      background: #007bff; 
      color: white; 
      border: none; 
      border-radius: 8px; 
      cursor: pointer;
      font-size: 16px;
      display: flex;
      align-items: center;
      gap: 8px;
      transition: all 0.3s ease;
    }
    
    .record-btn:hover {
      background: #0056b3;
    }
    
    .record-btn.listening { 
      background: #dc3545;
      animation: pulse 1.5s infinite;
    }
    
    .record-btn.listening:hover {
      background: #c82333;
    }
    
    .clear-btn {
      padding: 12px 20px;
      background: #6c757d;
      color: white;
      border: none;
      border-radius: 8px;
      cursor: pointer;
    }
    
    .clear-btn:disabled {
      background: #e9ecef;
      color: #6c757d;
      cursor: not-allowed;
    }
    
    .language-selector {
      margin-bottom: 20px;
    }
    
    .language-selector label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
    }
    
    .language-selector select {
      padding: 8px 12px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 14px;
    }
    
    .transcript { 
      margin-top: 20px; 
      padding: 20px; 
      background: #f8f9fa; 
      border-radius: 8px;
      border-left: 4px solid #007bff;
    }
    
    .transcript h4 {
      margin-top: 0;
      color: #495057;
    }
    
    .transcript p {
      margin-bottom: 0;
      line-height: 1.6;
      font-size: 16px;
    }
    
    .error { 
      color: #dc3545;
      background: #f8d7da;
      padding: 15px;
      border-radius: 8px;
      border: 1px solid #f5c6cb;
    }
    
    .listening-indicator {
      color: #28a745;
      font-weight: bold;
      text-align: center;
      margin: 10px 0;
    }
    
    .btn-icon {
      font-size: 18px;
    }
    
    @keyframes pulse {
      0% { transform: scale(1); }
      50% { transform: scale(1.05); }
      100% { transform: scale(1); }
    }
  `,
  ],
})
export class SpeechRecognitionComponent implements OnDestroy {
  transcript = ""
  isListening = false
  private subscription?: Subscription

  constructor(public speechService: SpeechRecognitionService) {}

  toggleListening(): void {
    if (this.isListening) {
      this.stopListening()
    } else {
      this.startListening()
    }
  }

  private startListening(): void {
    this.subscription = this.speechService.startListening().subscribe({
      next: (text: string) => {
        this.transcript += text + " "
      },
      error: (error) => {
        console.error("Speech recognition error:", error)
        this.isListening = false
      },
    })
    this.isListening = this.speechService.getIsListening()
  }

  private stopListening(): void {
    this.speechService.stopListening()
    this.isListening = false
    if (this.subscription) {
      this.subscription.unsubscribe()
    }
  }

  clearTranscript(): void {
    this.transcript = ""
  }

  changeLanguage(event: Event): void {
    const target = event.target as HTMLSelectElement
    this.speechService.setLanguage(target.value)
  }

  ngOnDestroy(): void {
    this.stopListening()
  }
}
