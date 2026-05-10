import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="container mt-4"><h1>AI Chatbot</h1><p>Chat with banking assistant here</p></div>`,
  styles: []
})
export class ChatbotComponent {}
