import { Component, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatResponse } from '../../core/services/chat.service';

interface ChatMessage {
  text: string;
  isUser: boolean;
  timestamp: Date;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <div class="card shadow">
            <div class="card-header bg-dark text-white d-flex align-items-center">
              <i class="bi bi-robot fs-4 me-2"></i>
              <h4 class="mb-0">AI Assistant</h4>
            </div>
            
            <div class="card-body chat-container bg-light" #scrollMe style="height: 500px; overflow-y: auto;">
              <div *ngIf="messages.length === 0" class="text-center text-muted my-5">
                <i class="bi bi-chat-dots display-1"></i>
                <p class="mt-3">Hello! I am your AI Bank Assistant. How can I help you today?</p>
              </div>

              <div *ngFor="let msg of messages" class="mb-3 d-flex" [ngClass]="msg.isUser ? 'justify-content-end' : 'justify-content-start'">
                <div class="message-bubble p-3 rounded-3 shadow-sm" [ngClass]="msg.isUser ? 'bg-primary text-white user-msg' : 'bg-white text-dark bot-msg'" style="max-width: 80%;">
                  <div class="d-flex align-items-center mb-1">
                    <i class="bi" [ngClass]="msg.isUser ? 'bi-person-circle' : 'bi-robot text-primary'" class="me-2"></i>
                    <small [ngClass]="msg.isUser ? 'text-light' : 'text-muted'">
                      {{ msg.isUser ? 'You' : 'Assistant' }} • {{ msg.timestamp | date:'shortTime' }}
                    </small>
                  </div>
                  <div style="white-space: pre-wrap;">{{ msg.text }}</div>
                </div>
              </div>

              <div *ngIf="loading" class="mb-3 d-flex justify-content-start">
                <div class="message-bubble p-3 rounded-3 shadow-sm bg-white text-dark bot-msg">
                  <div class="spinner-grow spinner-grow-sm text-primary me-1" role="status"></div>
                  <div class="spinner-grow spinner-grow-sm text-primary me-1" role="status"></div>
                  <div class="spinner-grow spinner-grow-sm text-primary" role="status"></div>
                </div>
              </div>
            </div>

            <div class="card-footer bg-white">
              <form (ngSubmit)="sendMessage()" class="d-flex">
                <input type="text" class="form-control me-2 rounded-pill" placeholder="Type your message..." 
                       [(ngModel)]="currentMessage" name="currentMessage" [disabled]="loading" autocomplete="off" autofocus>
                <button type="submit" class="btn btn-primary rounded-pill px-4" [disabled]="!currentMessage.trim() || loading">
                  <i class="bi bi-send"></i> Send
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .user-msg { border-bottom-right-radius: 4px !important; }
    .bot-msg { border-bottom-left-radius: 4px !important; }
    /* Hide scrollbar for Chrome, Safari and Opera */
    .chat-container::-webkit-scrollbar { width: 6px; }
    .chat-container::-webkit-scrollbar-track { background: transparent; }
    .chat-container::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.2); border-radius: 10px; }
  `]
})
export class ChatbotComponent implements AfterViewChecked {
  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;
  
  messages: ChatMessage[] = [];
  currentMessage: string = '';
  loading: boolean = false;

  constructor(private chatService: ChatService) {}

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }

  sendMessage(): void {
    if (!this.currentMessage.trim() || this.loading) return;

    const userText = this.currentMessage;
    this.messages.push({
      text: userText,
      isUser: true,
      timestamp: new Date()
    });
    this.currentMessage = '';
    this.loading = true;

    this.chatService.sendMessage(userText).subscribe({
      next: (res: ChatResponse) => {
        this.messages.push({
          text: res.answer,
          isUser: false,
          timestamp: new Date()
        });
        this.loading = false;
      },
      error: (err) => {
        this.messages.push({
          text: "Sorry, I am having trouble connecting to the server. Please try again later.",
          isUser: false,
          timestamp: new Date()
        });
        console.error('Chat error:', err);
        this.loading = false;
      }
    });
  }
}
