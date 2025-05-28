// src/app/services/chat.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatMessage  {
  sender: string;
  content: string;
  timestamp: string;
}

export interface Chat {
  id: string;
  patientId: string;
  createdAt: string;
  messages: ChatMessage[];
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8090/api/chats';

  constructor(private http: HttpClient) {}

  getChatHistory(userId: string): Observable<Chat> {
    return this.http.get<Chat>(`${this.apiUrl}/patient/${userId}`);
  }

  createNewChat(userId: string): Observable<Chat> {
    return this.http.post<Chat>(`${this.apiUrl}/new/${userId}`, {});
  }

  sendMessage(chatId: string, prompt: string): Observable<Chat> {
    return this.http.post<Chat>(`${this.apiUrl}/${chatId}/prompt`, { prompt });
  }
}
