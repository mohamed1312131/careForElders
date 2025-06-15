// src/app/services/websocket.service.ts
import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private client: Client;
  private subscriptions: Map<string, any> = new Map();

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8090/ws-chat'),
      reconnectDelay: 5000,
    });

    this.client.onConnect = () => {
      console.log('WebSocket connected');
      // Re-subscribe after reconnect if needed
      this.subscriptions.forEach((callback, chatId) => {
        this.client.subscribe(`/topic/chat/${chatId}`, (message: Message) => {
          callback(JSON.parse(message.body));
        });
      });
    };

    this.client.activate();
  }

  subscribeToChat(chatId: string, callback: (msg: any) => void) {
    if (this.client.connected) {
      this.client.subscribe(`/topic/chat/${chatId}`, (message: Message) => {
        callback(JSON.parse(message.body));
      });
    }

    // Save subscription handler for reconnect
    this.subscriptions.set(chatId, callback);
  }

  sendMessage(chatId: string, content: string) {
    this.client.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify({ chatId, content }),
    });
  }
}
