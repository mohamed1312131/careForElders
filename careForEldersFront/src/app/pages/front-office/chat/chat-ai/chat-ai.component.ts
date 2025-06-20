import { Component, OnInit } from '@angular/core';
import { Chat, ChatService, Message } from '../ChatService';


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

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.userId = localStorage.getItem('user_id');
    if (!this.userId) {
      console.warn('No user ID found in localStorage');
      // You might want to add redirect logic here
      return;
    }
  }

  startNewChat(): void {
    if (!this.userId) return;

    this.chatService.createNewChat(this.userId).subscribe(chat => {
      this.chat = chat;
    });
  }

  sendMessage(): void {
    if (!this.chat || !this.newMessage.trim()) return;

    const prompt = this.newMessage.trim();
    this.isLoading = true;

    this.chatService.sendMessage(this.chat.id, prompt).subscribe(updatedChat => {
      this.chat = updatedChat;
      this.newMessage = '';
      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    });
  }

  isPatientMessage(message: Message): boolean {
    return message.sender === 'patient';
  }

  isAIMessage(message: Message): boolean {
    return message.sender === 'ai';
  }
}
