import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Chat, ChatService, ChatMessage } from '../ChatService';
import { WebSocketService } from '../WebSocketService';

@Component({
  selector: 'app-chat-ai',
  templateUrl: './chat-ai.component.html',
  styleUrls: ['./chat-ai.component.scss']
})
export class ChatAIComponent implements OnInit {
  userId: string | null = null;
  chat: Chat | null = null;
  newMessage: string = '';
  isLoading = false;

  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  constructor(
    private chatService: ChatService,
    private wsService: WebSocketService
  ) {}

  ngOnInit(): void {
    this.userId = localStorage.getItem('user_id');
    console.log('[INIT] User ID:', this.userId);

    if (!this.userId) {
      console.warn('No user ID found in localStorage');
      return;
    }
  }

  startNewChat(): void {
    if (!this.userId) return;

    console.log('[CHAT] Creating new chat for user:', this.userId);
    this.chatService.createNewChat(this.userId).subscribe(chat => {
      this.chat = chat;
      console.log('[CHAT] New chat created:', chat);

      this.wsService.subscribeToChat(chat.id, (message: ChatMessage) => {
        console.log('[WS] New message received via WebSocket:', message);

        this.chat?.messages.push(message);
        if (message.sender === 'AI') {
          console.log('[WS] AI responded, stopping loader');
          this.isLoading = false;
        }

        this.scrollToBottom();
      });
    });
  }

  sendMessage(): void {
    if (!this.chat || !this.newMessage.trim()) return;

    const message: ChatMessage = {
      sender: 'PATIENT', // match backend
      content: this.newMessage.trim(),
      timestamp: new Date().toISOString()
    };

    console.log('[SEND] User message:', message);

    this.chat.messages.push(message);
    this.newMessage = '';
    this.isLoading = true;

    this.scrollToBottom();

    // Send message content only
    this.wsService.sendMessage(this.chat.id, message.content);
    console.log('[WS] Message sent to WebSocket:', message.content);
  }

  scrollToBottom(): void {
    setTimeout(() => {
      if (this.scrollContainer) {
        const el = this.scrollContainer.nativeElement;
        el.scrollTop = el.scrollHeight;
        console.log('[UI] Scrolled to bottom');
      }
    }, 100);
  }

  isPatientMessage(message: ChatMessage): boolean {
    const isPatient = message.sender === 'PATIENT';
    console.log('[CHECK] isPatientMessage:', message, isPatient);
    return isPatient;
  }

  isAIMessage(message: ChatMessage): boolean {
    const isAI = message.sender === 'AI';
    console.log('[CHECK] isAIMessage:', message, isAI);
    return isAI;
  }
}
