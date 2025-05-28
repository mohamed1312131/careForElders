import {
  Component,
  EventEmitter,
  Input,
  Output,
   OnInit,
   OnDestroy,
  ChangeDetectionStrategy,
   ChangeDetectorRef,
} from "@angular/core"
import { Subscription } from "rxjs"
import  { MatSnackBar } from "@angular/material/snack-bar"
import {
  SpeechToTextService,
  RecordingState,
  AudioConstraints,
  BrowserSupport,
} from "../speech-to-text.service"

@Component({
  selector: "app-speech-to-text",
  templateUrl: "./speech-to-text.component.html",
  styleUrls: ["./speech-to-text.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SpeechToTextComponent implements OnInit, OnDestroy {
  @Input() disabled = false
  @Input() languageCode = "en-US"
  @Input() maxDuration = 60
  @Input() showLanguageSelector = true
  @Input() showTips = true
  @Input() showBrowserSupport = true
  @Input() audioConstraints: Partial<AudioConstraints> = {}

  @Output() transcriptReceived = new EventEmitter<string>()
  @Output() recordingStateChanged = new EventEmitter<RecordingState>()
  @Output() recordingStarted = new EventEmitter<void>()
  @Output() recordingStopped = new EventEmitter<Blob>()
  @Output() transcriptionCompleted = new EventEmitter<{ transcript: string; success: boolean }>()

  recordingState: RecordingState = {
    isRecording: false,
    isProcessing: false,
    duration: 0,
    error: null,
    maxDurationReached: false,
  }

  browserSupport: BrowserSupport = {
    mediaDevices: false,
    mediaRecorder: false,
    audioContext: false,
    secureContext: false,
  }

  private subscription = new Subscription()
  private audioBlob: Blob | null = null

  // Language options with more comprehensive list
  languageOptions = [
    { code: "en-US", name: "English (US)", flag: "🇺🇸" },
    { code: "en-GB", name: "English (UK)", flag: "🇬🇧" },
    { code: "es-ES", name: "Spanish (Spain)", flag: "🇪🇸" },
    { code: "es-MX", name: "Spanish (Mexico)", flag: "🇲🇽" },
    { code: "fr-FR", name: "French", flag: "🇫🇷" },
    { code: "de-DE", name: "German", flag: "🇩🇪" },
    { code: "it-IT", name: "Italian", flag: "🇮🇹" },
    { code: "pt-BR", name: "Portuguese (Brazil)", flag: "🇧🇷" },
    { code: "pt-PT", name: "Portuguese (Portugal)", flag: "🇵🇹" },
    { code: "ja-JP", name: "Japanese", flag: "🇯🇵" },
    { code: "ko-KR", name: "Korean", flag: "🇰🇷" },
    { code: "zh-CN", name: "Chinese (Simplified)", flag: "🇨🇳" },
    { code: "zh-TW", name: "Chinese (Traditional)", flag: "🇹🇼" },
    { code: "ru-RU", name: "Russian", flag: "🇷🇺" },
    { code: "ar-SA", name: "Arabic", flag: "🇸🇦" },
    { code: "hi-IN", name: "Hindi", flag: "🇮🇳" },
    { code: "nl-NL", name: "Dutch", flag: "🇳🇱" },
    { code: "sv-SE", name: "Swedish", flag: "🇸🇪" },
    { code: "da-DK", name: "Danish", flag: "🇩🇰" },
    { code: "no-NO", name: "Norwegian", flag: "🇳🇴" },
    { code: "fi-FI", name: "Finnish", flag: "🇫🇮" },
  ]

  constructor(
    private speechToTextService: SpeechToTextService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // Check browser support
    this.browserSupport = this.speechToTextService.getBrowserSupport()

    // Show browser support warnings if needed
    this.checkBrowserCompatibility()

    this.subscription.add(
      this.speechToTextService.recordingState$.subscribe((state) => {
        this.recordingState = state
        this.recordingStateChanged.emit(state)

        // Handle max duration reached
        if (state.maxDurationReached) {
          this.snackBar.open(`Recording stopped automatically after ${this.maxDuration} seconds`, "Close", {
            duration: 4000,
            panelClass: ["warning-snackbar"],
          })
        }

        // Handle errors
        if (state.error) {
          this.handleError(state.error)
        }

        this.cdr.markForCheck()
      }),
    )
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
    this.speechToTextService.cancelRecording()
  }

  async startRecording(): Promise<void> {
    if (!this.isRecordingSupported) {
      this.showUnsupportedError()
      return
    }

    try {
      await this.speechToTextService.startRecording(this.audioConstraints)
      this.recordingStarted.emit()

      this.snackBar.open("Recording started", "Close", {
        duration: 2000,
        panelClass: ["success-snackbar"],
      })
    } catch (error) {
      console.error("Failed to start recording:", error)
      this.handleError("Failed to start recording. Please check your microphone permissions.")
    }
  }

  async stopRecording(): Promise<void> {
    try {
      this.audioBlob = await this.speechToTextService.stopRecording()
      this.recordingStopped.emit(this.audioBlob)

      // Auto-transcribe after stopping
      await this.transcribeAudio()
    } catch (error) {
      console.error("Failed to stop recording:", error)
      this.handleError("Failed to stop recording")
    }
  }

  async transcribeAudio(): Promise<void> {
    if (!this.audioBlob) {
      this.snackBar.open("No audio recorded", "Close", { duration: 3000 })
      return
    }

    try {
      const transcript = await this.speechToTextService.transcribeAudio(this.audioBlob, this.languageCode)

      if (transcript.trim()) {
        this.transcriptReceived.emit(transcript)
        this.transcriptionCompleted.emit({ transcript, success: true })

        this.snackBar.open("Speech transcribed successfully!", "Close", {
          duration: 3000,
          panelClass: ["success-snackbar"],
        })

        // Clear audio blob after successful transcription
        this.audioBlob = null
      } else {
        this.transcriptionCompleted.emit({ transcript: "", success: false })
        this.snackBar.open("No speech detected. Please try again.", "Close", {
          duration: 4000,
          panelClass: ["warning-snackbar"],
        })
      }
    } catch (error) {
      console.error("Transcription failed:", error)
      this.transcriptionCompleted.emit({ transcript: "", success: false })
      this.handleError("Failed to transcribe speech. Please try again.")
    }
  }

  cancelRecording(): void {
    this.speechToTextService.cancelRecording()
    this.audioBlob = null

    this.snackBar.open("Recording cancelled", "Close", {
      duration: 2000,
      panelClass: ["info-snackbar"],
    })
  }

  retryTranscription(): void {
    if (this.audioBlob) {
      this.transcribeAudio()
    } else {
      this.snackBar.open("No audio available to retry", "Close", { duration: 3000 })
    }
  }

  clearError(): void {
    if (this.recordingState.error) {
      this.speechToTextService.cancelRecording()
    }
  }

  // Getters for template
  get isRecordingSupported(): boolean {
    return this.speechToTextService.isRecordingSupported()
  }

  get formattedDuration(): string {
    return this.speechToTextService.formatDuration(this.recordingState.duration)
  }

  get remainingTime(): string {
    const remaining = this.maxDuration - this.recordingState.duration
    return remaining > 0 ? this.speechToTextService.formatDuration(remaining) : "0:00"
  }

  get canStartRecording(): boolean {
    return (
      !this.disabled &&
      !this.recordingState.isRecording &&
      !this.recordingState.isProcessing &&
      this.isRecordingSupported
    )
  }

  get canStopRecording(): boolean {
    return this.recordingState.isRecording && !this.recordingState.isProcessing
  }

  get canRetry(): boolean {
    return !this.recordingState.isRecording && !this.recordingState.isProcessing && this.audioBlob !== null
  }

  get isNearMaxDuration(): boolean {
    return this.recordingState.duration >= this.maxDuration - 10
  }

  get recordingProgress(): number {
    return (this.recordingState.duration / this.maxDuration) * 100
  }

  get selectedLanguage(): { code: string; name: string; flag: string } | undefined {
    return this.languageOptions.find((lang) => lang.code === this.languageCode)
  }

  get supportedLanguages(): string[] {
    return this.speechToTextService.getSupportedLanguages()
  }

  get browserSupportMessage(): string {
    const issues: string[] = []

    if (!this.browserSupport.secureContext) {
      issues.push("HTTPS required")
    }
    if (!this.browserSupport.mediaDevices) {
      issues.push("MediaDevices API not supported")
    }
    if (!this.browserSupport.mediaRecorder) {
      issues.push("MediaRecorder API not supported")
    }
    if (!this.browserSupport.audioContext) {
      issues.push("AudioContext not supported")
    }

    return issues.length > 0 ? `Issues: ${issues.join(", ")}` : "All features supported"
  }

  // Private methods
  private checkBrowserCompatibility(): void {
    if (!this.browserSupport.secureContext) {
      this.snackBar.open("Recording requires HTTPS. Some features may not work on HTTP.", "Close", {
        duration: 8000,
        panelClass: ["warning-snackbar"],
      })
    }

    if (!this.browserSupport.mediaDevices || !this.browserSupport.mediaRecorder) {
      this.snackBar.open(
        "Your browser doesn't fully support audio recording. Please use Chrome, Firefox, or Safari.",
        "Close",
        {
          duration: 8000,
          panelClass: ["error-snackbar"],
        },
      )
    }
  }

  private handleError(message: string): void {
    this.snackBar.open(message, "Close", {
      duration: 5000,
      panelClass: ["error-snackbar"],
    })
  }

  private showUnsupportedError(): void {
    this.snackBar.open("Speech recording is not supported in this browser or requires HTTPS.", "Close", {
      duration: 7000,
      panelClass: ["error-snackbar"],
    })
  }
}
