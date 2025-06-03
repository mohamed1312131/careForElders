import { Component, OnDestroy, OnInit } from "@angular/core"
import { SpeechRecognitionService } from "./speech-recognition.service"
import { Subscription } from "rxjs"

@Component({
  selector: "app-speech-to-text",
  templateUrl: './speech-recognition.component.html',
  styleUrls: ['./speech-recognition.component.scss']
})
export class SpeechRecognitionComponent implements OnInit, OnDestroy {
  transcript = ""
  interimTranscript = ""
  isListening = false
  lastConfidence = 0
  private subscriptions: Subscription[] = []

  constructor(public speechService: SpeechRecognitionService) {}

  ngOnInit(): void {
    // Subscribe to listening status
    this.subscriptions.push(
      this.speechService.getListeningStatus().subscribe(status => {
        this.isListening = status
      })
    )

    // Subscribe to interim transcript
    this.subscriptions.push(
      this.speechService.getInterimTranscript().subscribe(interim => {
        this.interimTranscript = interim
      })
    )

    // Subscribe to final transcript
    this.subscriptions.push(
      this.speechService.getFinalTranscript().subscribe(final => {
        this.transcript = final
        this.interimTranscript = "" // Clear interim when we get final
      })
    )
  }

  toggleListening(): void {
    if (this.isListening) {
      this.stopListening()
    } else {
      this.startListening()
    }
  }

  private startListening(): void {
    this.subscriptions.push(
      this.speechService.startListening().subscribe({
        next: (text: string) => {
          this.transcript = text
        },
        error: (error) => {
          console.error("Speech recognition error:", error)
          this.isListening = false
        },
      })
    )
  }

  private stopListening(): void {
    this.speechService.stopListening()
    this.isListening = false
  }

  clearTranscript(): void {
    this.transcript = ""
    this.interimTranscript = ""
    this.speechService.clearTranscript()
  }

  changeLanguage(event: Event): void {
    const target = event.target as HTMLSelectElement
    this.speechService.setLanguage(target.value)
  }

  getWordCount(): number {
    return this.transcript.trim().split(/\s+/).filter(word => word.length > 0).length
  }

  ngOnDestroy(): void {
    this.stopListening()
    this.subscriptions.forEach(sub => sub.unsubscribe())
  }
}