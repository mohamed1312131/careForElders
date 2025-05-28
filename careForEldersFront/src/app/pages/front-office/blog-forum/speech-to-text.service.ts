import { Injectable } from "@angular/core"
import  { HttpClient, HttpErrorResponse } from "@angular/common/http"
import { BehaviorSubject, type Observable, throwError, firstValueFrom } from "rxjs"
import { catchError, timeout } from "rxjs/operators"

export interface SpeechToTextResponse {
  transcript: string
  confidence?: number
  alternatives?: Array<{
    transcript: string
    confidence: number
  }>
}

export interface RecordingState {
  isRecording: boolean
  isProcessing: boolean
  duration: number
  error: string | null
  maxDurationReached?: boolean
}

export interface AudioConstraints {
  echoCancellation: boolean
  noiseSuppression: boolean
  autoGainControl: boolean
  sampleRate: number
  channelCount: number
}

export interface BrowserSupport {
  mediaDevices: boolean
  mediaRecorder: boolean
  audioContext: boolean
  secureContext: boolean
}

@Injectable({
  providedIn: "root",
})
export class SpeechToTextService {
  private apiUrl = "http://localhost:8084/api/speech-to-text"  
    private readonly maxRecordingDuration = 60 // seconds
  private readonly requestTimeout = 30000 // 30 seconds

  private mediaRecorder: MediaRecorder | null = null
  private audioChunks: Blob[] = []
  private recordingStateSubject = new BehaviorSubject<RecordingState>({
    isRecording: false,
    isProcessing: false,
    duration: 0,
    error: null,
    maxDurationReached: false,
  })

  public readonly recordingState$ = this.recordingStateSubject.asObservable()
  private recordingTimer: number | null = null
  private startTime = 0
  private currentStream: MediaStream | null = null

  constructor(private http: HttpClient) {}

  async startRecording(constraints?: Partial<AudioConstraints>): Promise<void> {
    try {
      // Check browser support before starting
      const support = this.getBrowserSupport()
      if (!support.secureContext) {
        throw new Error("Recording requires a secure context (HTTPS)")
      }
      if (!support.mediaDevices) {
        throw new Error("MediaDevices API is not supported")
      }
      if (!support.mediaRecorder) {
        throw new Error("MediaRecorder API is not supported")
      }

      // Check if already recording
      if (this.recordingStateSubject.value.isRecording) {
        throw new Error("Recording is already in progress")
      }

      // Get user media with enhanced constraints
      const audioConstraints: AudioConstraints = {
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true,
        sampleRate: 16000,
        channelCount: 1,
        ...constraints,
      }

      this.currentStream = await navigator.mediaDevices.getUserMedia({
        audio: audioConstraints,
      })

      // Initialize MediaRecorder with best available format
      const mimeType = this.getBestSupportedMimeType()
      this.mediaRecorder = new MediaRecorder(this.currentStream, {
        mimeType,
        audioBitsPerSecond: 128000,
      })

      this.setupMediaRecorderEvents()
      this.audioChunks = []
      this.mediaRecorder.start(100) // Collect data every 100ms

      this.startTime = Date.now()
      this.startTimer()

      this.updateRecordingState({
        isRecording: true,
        isProcessing: false,
        duration: 0,
        error: null,
        maxDurationReached: false,
      })
    } catch (error) {
      console.error("Error starting recording:", error)
      await this.cleanup()

      let errorMessage = "Failed to start recording"
      if (error instanceof Error) {
        if (error.name === "NotAllowedError") {
          errorMessage = "Microphone access denied. Please allow microphone permissions."
        } else if (error.name === "NotFoundError") {
          errorMessage = "No microphone found. Please connect a microphone."
        } else if (error.name === "NotSupportedError") {
          errorMessage = "Audio recording is not supported in this browser."
        } else if (error.message.includes("secure context")) {
          errorMessage = "Recording requires HTTPS. Please use a secure connection."
        } else {
          errorMessage = error.message
        }
      }

      this.updateRecordingState({
        isRecording: false,
        isProcessing: false,
        duration: 0,
        error: errorMessage,
        maxDurationReached: false,
      })

      throw error
    }
  }

  async stopRecording(): Promise<Blob> {
    return new Promise((resolve, reject) => {
      if (!this.mediaRecorder || this.mediaRecorder.state === "inactive") {
        reject(new Error("No active recording"))
        return
      }

      const handleStop = () => {
        const mimeType = this.mediaRecorder?.mimeType || "audio/webm"
        const audioBlob = new Blob(this.audioChunks, { type: mimeType })

        this.cleanup()
        this.updateRecordingState({
          isRecording: false,
          isProcessing: false,
          duration: this.recordingStateSubject.value.duration,
          error: null,
          maxDurationReached: false,
        })

        resolve(audioBlob)
      }

      this.mediaRecorder.addEventListener("stop", handleStop, { once: true })
      this.mediaRecorder.stop()
    })
  }

  async transcribeAudio(audioBlob: Blob, languageCode = "en-US"): Promise<string> {
    this.updateRecordingState({
      ...this.recordingStateSubject.value,
      isProcessing: true,
      error: null,
    })

    try {
      // Convert to WAV for better compatibility
      const wavBlob = await this.convertToWav(audioBlob)

      // Validate audio blob
      if (wavBlob.size === 0) {
        throw new Error("Audio file is empty")
      }

      const formData = new FormData()
      formData.append("audio", wavBlob, "recording.wav")
      formData.append("language", languageCode)

      // Use modern RxJS pattern instead of deprecated toPromise()
      const response = await firstValueFrom(
        this.http
          .post<SpeechToTextResponse>(`${this.apiUrl}/speech-to-text`, formData)
          .pipe(timeout(this.requestTimeout), catchError(this.handleHttpError.bind(this))),
      )

      this.updateRecordingState({
        ...this.recordingStateSubject.value,
        isProcessing: false,
        error: null,
      })

      return response?.transcript || ""
    } catch (error) {
      console.error("Error transcribing audio:", error)

      let errorMessage = "Failed to transcribe audio. Please try again."
      if (error instanceof Error) {
        if (error.message.includes("timeout")) {
          errorMessage = "Transcription timed out. Please try with a shorter recording."
        } else if (error.message.includes("network")) {
          errorMessage = "Network error. Please check your connection and try again."
        }
      }

      this.updateRecordingState({
        ...this.recordingStateSubject.value,
        isProcessing: false,
        error: errorMessage,
      })

      throw error
    }
  }

  cancelRecording(): void {
    if (this.mediaRecorder && this.mediaRecorder.state === "recording") {
      this.mediaRecorder.stop()
    }

    this.cleanup()
    this.updateRecordingState({
      isRecording: false,
      isProcessing: false,
      duration: 0,
      error: null,
      maxDurationReached: false,
    })
  }

  isRecordingSupported(): boolean {
    const support = this.getBrowserSupport()
    return support.mediaDevices && support.mediaRecorder && support.secureContext
  }

  getBrowserSupport(): BrowserSupport {
    const hasNavigator = typeof navigator !== "undefined"
    const hasWindow = typeof window !== "undefined"

    return {
      mediaDevices:
        hasNavigator && !!navigator.mediaDevices && typeof navigator.mediaDevices.getUserMedia === "function",
      mediaRecorder:
        hasWindow && typeof MediaRecorder !== "undefined" && typeof MediaRecorder.isTypeSupported === "function",
      audioContext:
        hasWindow && (typeof AudioContext !== "undefined" || typeof (window as any).webkitAudioContext !== "undefined"),
      secureContext:
        hasWindow && (window.isSecureContext || location.protocol === "https:" || location.hostname === "localhost"),
    }
  }

  formatDuration(seconds: number): string {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins}:${secs.toString().padStart(2, "0")}`
  }

  getCurrentState(): RecordingState {
    return this.recordingStateSubject.value
  }

  getSupportedLanguages(): string[] {
    // Return list of supported language codes
    return [
      "en-US",
      "en-GB",
      "es-ES",
      "es-MX",
      "fr-FR",
      "de-DE",
      "it-IT",
      "pt-BR",
      "pt-PT",
      "ja-JP",
      "ko-KR",
      "zh-CN",
      "zh-TW",
      "ru-RU",
      "ar-SA",
      "hi-IN",
      "nl-NL",
      "sv-SE",
      "da-DK",
      "no-NO",
      "fi-FI",
    ]
  }

  // Private methods
  private setupMediaRecorderEvents(): void {
    if (!this.mediaRecorder) return

    this.mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0) {
        this.audioChunks.push(event.data)
      }
    }

    this.mediaRecorder.onerror = (event) => {
      console.error("MediaRecorder error:", event)
      this.updateRecordingState({
        ...this.recordingStateSubject.value,
        error: "Recording error occurred",
      })
    }
  }

  private getBestSupportedMimeType(): string {
    const types = [
      "audio/webm;codecs=opus",
      "audio/webm",
      "audio/mp4;codecs=mp4a.40.2",
      "audio/mp4",
      "audio/wav",
      "audio/ogg;codecs=opus",
    ]

    // Check if MediaRecorder.isTypeSupported exists before using it
    if (typeof MediaRecorder !== "undefined" && typeof MediaRecorder.isTypeSupported === "function") {
      for (const type of types) {
        try {
          if (MediaRecorder.isTypeSupported(type)) {
            return type
          }
        } catch (error) {
          console.warn(`Error checking support for ${type}:`, error)
        }
      }
    }

    return "audio/webm" // fallback
  }

  private async convertToWav(audioBlob: Blob): Promise<Blob> {
    try {
      const support = this.getBrowserSupport()
      if (!support.audioContext) {
        console.warn("AudioContext not supported, using original audio format")
        return audioBlob
      }

      const AudioContextClass = window.AudioContext || (window as any).webkitAudioContext
      const audioContext = new AudioContextClass({
        sampleRate: 16000,
      })

      const arrayBuffer = await audioBlob.arrayBuffer()
      const audioBuffer = await audioContext.decodeAudioData(arrayBuffer)

      // Convert to WAV format
      const wavBuffer = this.audioBufferToWav(audioBuffer)
      await audioContext.close()

      return new Blob([wavBuffer], { type: "audio/wav" })
    } catch (error) {
      console.warn("Failed to convert audio format, using original:", error)
      return audioBlob
    }
  }

  private audioBufferToWav(buffer: AudioBuffer): ArrayBuffer {
    const length = buffer.length
    const numberOfChannels = Math.min(buffer.numberOfChannels, 2) // Limit to stereo
    const sampleRate = buffer.sampleRate
    const arrayBuffer = new ArrayBuffer(44 + length * numberOfChannels * 2)
    const view = new DataView(arrayBuffer)

    // WAV header
    const writeString = (offset: number, string: string) => {
      for (let i = 0; i < string.length; i++) {
        view.setUint8(offset + i, string.charCodeAt(i))
      }
    }

    writeString(0, "RIFF")
    view.setUint32(4, 36 + length * numberOfChannels * 2, true)
    writeString(8, "WAVE")
    writeString(12, "fmt ")
    view.setUint32(16, 16, true)
    view.setUint16(20, 1, true)
    view.setUint16(22, numberOfChannels, true)
    view.setUint32(24, sampleRate, true)
    view.setUint32(28, sampleRate * numberOfChannels * 2, true)
    view.setUint16(32, numberOfChannels * 2, true)
    view.setUint16(34, 16, true)
    writeString(36, "data")
    view.setUint32(40, length * numberOfChannels * 2, true)

    // Convert audio data
    let offset = 44
    for (let i = 0; i < length; i++) {
      for (let channel = 0; channel < numberOfChannels; channel++) {
        const sample = Math.max(-1, Math.min(1, buffer.getChannelData(channel)[i]))
        view.setInt16(offset, sample < 0 ? sample * 0x8000 : sample * 0x7fff, true)
        offset += 2
      }
    }

    return arrayBuffer
  }

  private startTimer(): void {
    this.recordingTimer = window.setInterval(() => {
      const duration = Math.floor((Date.now() - this.startTime) / 1000)

      // Auto-stop if max duration reached
      if (duration >= this.maxRecordingDuration) {
        this.stopRecording().catch(console.error)
        this.updateRecordingState({
          ...this.recordingStateSubject.value,
          duration,
          maxDurationReached: true,
        })
        return
      }

      this.updateRecordingState({
        ...this.recordingStateSubject.value,
        duration,
      })
    }, 1000)
  }

  private clearTimer(): void {
    if (this.recordingTimer) {
      clearInterval(this.recordingTimer)
      this.recordingTimer = null
    }
  }

  private async cleanup(): Promise<void> {
    this.clearTimer()

    if (this.currentStream) {
      this.currentStream.getTracks().forEach((track) => track.stop())
      this.currentStream = null
    }

    this.mediaRecorder = null
    this.audioChunks = []
  }

  private updateRecordingState(state: RecordingState): void {
    this.recordingStateSubject.next(state)
  }

  private handleHttpError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = "An error occurred during transcription"

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Network error: ${error.error.message}`
    } else {
      // Server-side error
      switch (error.status) {
        case 400:
          errorMessage = "Invalid audio format or request"
          break
        case 401:
          errorMessage = "Authentication failed"
          break
        case 413:
          errorMessage = "Audio file too large"
          break
        case 429:
          errorMessage = "Too many requests. Please try again later"
          break
        case 500:
          errorMessage = "Server error. Please try again later"
          break
        default:
          errorMessage = `Server error: ${error.status}`
      }
    }

    return throwError(() => new Error(errorMessage))
  }
}
