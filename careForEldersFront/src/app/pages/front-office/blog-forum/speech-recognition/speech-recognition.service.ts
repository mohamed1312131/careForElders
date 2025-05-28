// speech-recognition.service.ts
import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class SpeechRecognitionService {
  private recognition: any;
  private speechSubject = new Subject<string>();
  private finalTranscript = '';
  private audioChunks: Blob[] = [];
  private mediaRecorder: MediaRecorder | null = null;
  private stream: MediaStream | null = null;

  constructor(private http: HttpClient) {}

  public async startListening(): Promise<void> {
    this.finalTranscript = '';
    this.audioChunks = [];
    
    try {
      // Initialize Web Speech API
      this.initSpeechRecognition();
      
      // Initialize audio recording
      await this.initAudioRecording();
      
      this.recognition.start();
    } catch (error) {
      console.error('Error initializing speech recognition:', error);
      throw error;
    }
  }

  public stopListening(): void {
    if (this.recognition) {
      this.recognition.stop();
    }
    if (this.mediaRecorder) {
      this.mediaRecorder.stop();
    }
    this.closeAudioStream();
  }

  public getTranscript(): Observable<string> {
    return this.speechSubject.asObservable();
  }

  private initSpeechRecognition(): void {
    // @ts-ignore
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    this.recognition = new SpeechRecognition();
    this.recognition.continuous = true;
    this.recognition.interimResults = true;

    this.recognition.onresult = (event: any) => {
      let interimTranscript = '';
      
      for (let i = event.resultIndex; i < event.results.length; i++) {
        const transcript = event.results[i][0].transcript;
        if (event.results[i].isFinal) {
          this.finalTranscript += transcript + ' ';
        } else {
          interimTranscript += transcript;
        }
      }
      
      // Emit combined results
      this.speechSubject.next(this.finalTranscript + interimTranscript);
    };

    this.recognition.onend = () => {
      this.processFinalAudio();
    };
  }

  private async initAudioRecording(): Promise<void> {
    this.stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    this.mediaRecorder = new MediaRecorder(this.stream);
    
    this.mediaRecorder.ondataavailable = (event: BlobEvent) => {
      if (event.data.size > 0) {
        this.audioChunks.push(event.data);
      }
    };
    
    this.mediaRecorder.start(1000); // Collect data every 1 second
  }

  private async processFinalAudio(): Promise<void> {
    if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
      this.mediaRecorder.stop();
      return;
    }

    try {
      const audioBlob = new Blob(this.audioChunks, { type: 'audio/wav' });
      const formData = new FormData();
      formData.append('audio', audioBlob, 'recording.wav');
      formData.append('language', 'en-US'); // Can be dynamic based on user selection

      // Send to Spring Boot backend
      const response = await this.http.post<any>('/api/speech-to-text', formData).toPromise();
      this.speechSubject.next(response.transcript);
    } catch (error) {
      console.error('Error processing audio:', error);
      this.speechSubject.error('Error processing speech');
    } finally {
      this.closeAudioStream();
    }
  }

  private closeAudioStream(): void {
    if (this.stream) {
      this.stream.getTracks().forEach(track => track.stop());
      this.stream = null;
    }
  }
}