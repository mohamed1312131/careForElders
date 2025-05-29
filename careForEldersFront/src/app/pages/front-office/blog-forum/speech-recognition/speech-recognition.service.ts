// src/app/services/speech-recognition.service.ts
import { Injectable } from "@angular/core"
import { Observable, Subject } from "rxjs"

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

  constructor() {
    this.initializeRecognition()
  }

  private initializeRecognition() {
    if (this.isSupported()) {
      // Use proper type casting to avoid TypeScript errors
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

      for (let i = event.resultIndex; i < event.results.length; i++) {
        const result = event.results[i]
        if (result.isFinal) {
          finalTranscript += result[0].transcript
        }
      }

      if (finalTranscript.trim()) {
        this.transcriptionSubject.next(finalTranscript.trim())
      }
    }

    this.recognition.onerror = (event: any) => {
      console.error("Speech recognition error:", event.error)
      this.isListening = false

      // Handle specific errors
      if (event.error === "not-allowed") {
        alert("Microphone access denied. Please allow microphone access and try again.")
      } else if (event.error === "no-speech") {
        console.log("No speech detected. Try speaking again.")
      }
    }

    this.recognition.onend = () => {
      this.isListening = false
    }

    this.recognition.onstart = () => {
      this.isListening = true
    }
  }

  startListening(): Observable<string> {
    if (this.recognition && !this.isListening) {
      try {
        this.recognition.start()
      } catch (error) {
        console.error("Error starting speech recognition:", error)
      }
    }
    return this.transcriptionSubject.asObservable()
  }

  stopListening(): void {
    if (this.recognition && this.isListening) {
      this.recognition.stop()
    }
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
}
