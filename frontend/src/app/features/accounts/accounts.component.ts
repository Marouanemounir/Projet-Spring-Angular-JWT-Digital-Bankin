import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="container mt-4"><h1>Bank Accounts</h1><p>Bank accounts management page</p></div>`,
  styles: []
})
export class AccountsComponent {}
