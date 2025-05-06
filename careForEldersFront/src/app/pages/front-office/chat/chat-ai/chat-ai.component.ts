import { Component, OnInit } from '@angular/core';
import { Chat, ChatService, Message } from '../ChatService';


@Component({
  selector: 'app-chat-ai',
  templateUrl: './chat-ai.component.html',
  styleUrls: ['./chat-ai.component.scss']
})
export class ChatAIComponent implements OnInit {
  userId = '680a2319bd2b9864caf53529'; // Replace with dynamic patient ID as needed
  chat: Chat | null = null;
  newMessage: string = '';
  isLoading = false;

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    // Optional: fetch existing chats if needed
  }

  startNewChat(): void {
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
