// src/app/services/speech-recognition.service.ts
import { Injectable } from "@angular/core"
import { Observable, Subject, BehaviorSubject } from "rxjs"

declare global {
  interface Window {
    SpeechRecognition: any
    webkitSpeechRecognition: any
  }
}

export interface SpeechRecognitionEvent {
  resultIndex: number
  results: SpeechRecognitionResultList
}

export interface SpeechRecognitionResultList {
  length: number
  item(index: number): SpeechRecognitionResult
  [index: number]: SpeechRecognitionResult
}

export interface SpeechRecognitionResult {
  length: number
  item(index: number): SpeechRecognitionAlternative
  [index: number]: SpeechRecognitionAlternative
  isFinal: boolean
}

export interface SpeechRecognitionAlternative {
  transcript: string
  confidence: number
}

@Injectable({
  providedIn: "root",
})
export class SpeechRecognitionService {
  private recognition: any
  private isListening = false
  private transcriptionSubject = new Subject<string>()
  private interimTranscriptSubject = new Subject<string>()
  private finalTranscriptSubject = new Subject<string>()
  private isListeningSubject = new BehaviorSubject<boolean>(false)
  private currentTranscript = ""

  constructor() {
    this.initializeRecognition()
  }

  private initializeRecognition() {
    if (this.isSupported()) {
      const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition
      this.recognition = new SpeechRecognition()
      this.setupRecognition()
    }
  }

  private setupRecognition() {
    if (!this.recognition) return

    this.recognition.continuous = true
    this.recognition.interimResults = true
    this.recognition.lang = "en-US"
    this.recognition.maxAlternatives = 1

    this.recognition.onresult = (event: SpeechRecognitionEvent) => {
      let finalTranscript = ""
      let interimTranscript = ""

      for (let i = event.resultIndex; i < event.results.length; i++) {
        const result = event.results[i]
        const transcript = result[0].transcript

        if (result.isFinal) {
          finalTranscript += transcript
        } else {
          interimTranscript += transcript
        }
      }

      // Update current transcript with final results
      if (finalTranscript.trim()) {
        this.currentTranscript += finalTranscript
        this.finalTranscriptSubject.next(this.currentTranscript)
        this.transcriptionSubject.next(this.currentTranscript)
      }

      // Send interim results
      if (interimTranscript.trim()) {
        this.interimTranscriptSubject.next(interimTranscript)
      }
    }

    this.recognition.onerror = (event: any) => {
      console.error("Speech recognition error:", event.error)
      this.isListening = false
      this.isListeningSubject.next(false)

      // Handle specific errors
      if (event.error === "not-allowed") {
        this.transcriptionSubject.error("Microphone access denied. Please allow microphone access and try again.")
      } else if (event.error === "no-speech") {
        console.log("No speech detected. Try speaking again.")
      } else {
        this.transcriptionSubject.error(`Speech recognition error: ${event.error}`)
      }
    }

    this.recognition.onend = () => {
      this.isListening = false
      this.isListeningSubject.next(false)
    }

    this.recognition.onstart = () => {
      this.isListening = true
      this.isListeningSubject.next(true)
    }
  }

  startListening(): Observable<string> {
    if (this.recognition && !this.isListening) {
      try {
        this.currentTranscript = ""
        this.recognition.start()
      } catch (error) {
        console.error("Error starting speech recognition:", error)
        this.transcriptionSubject.error("Failed to start speech recognition")
      }
    }
    return this.transcriptionSubject.asObservable()
  }

  stopListening(): void {
    if (this.recognition && this.isListening) {
      this.recognition.stop()
    }
  }

  // Get real-time interim results
  getInterimTranscript(): Observable<string> {
    return this.interimTranscriptSubject.asObservable()
  }

  // Get final transcript updates
  getFinalTranscript(): Observable<string> {
    return this.finalTranscriptSubject.asObservable()
  }

  // Get listening status
  getListeningStatus(): Observable<boolean> {
    return this.isListeningSubject.asObservable()
  }

  // Clear current transcript
  clearTranscript(): void {
    this.currentTranscript = ""
    this.transcriptionSubject.next("")
    this.interimTranscriptSubject.next("")
    this.finalTranscriptSubject.next("")
  }

  isSupported(): boolean {
    return !!((window as any).SpeechRecognition || (window as any).webkitSpeechRecognition)
  }

  getIsListening(): boolean {
    return this.isListening
  }

  // Method to change language
  setLanguage(language: string): void {
    if (this.recognition) {
      this.recognition.lang = language
    }
  }

  // Get current transcript
  getCurrentTranscript(): string {
    return this.currentTranscript
  }
}