import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import * as SockJS from 'sockjs-client';
import { Client, Message } from '@stomp/stompjs';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  public bookingNotifications = new Subject<any>();
  private stompClient: Client;

  constructor() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8085/ws-notifications'),
      debug: (str) => console.log(str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.stompClient.onConnect = (frame) => {
      this.stompClient.subscribe('/topic/bookings', (message: Message) => {
        if (message.body) {
          this.bookingNotifications.next(JSON.parse(message.body));
        }
      });
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Broker error: ' + frame.headers['message']);
    };

    this.stompClient.activate(); // Pornim conexiunea
  }
}