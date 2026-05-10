import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="container mt-4"><h1>Customers</h1><p>Customers management page</p></div>`,
  styles: []
})
export class CustomersComponent {}
