import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container-fluid">
        <a class="navbar-brand" routerLink="/dashboard">Digital Banking</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item" *ngIf="isLoggedIn">
              <a class="nav-link" routerLink="/dashboard">Dashboard</a>
            </li>
            <li class="nav-item" *ngIf="isLoggedIn">
              <a class="nav-link" routerLink="/customers">Customers</a>
            </li>
            <li class="nav-item" *ngIf="isLoggedIn">
              <a class="nav-link" routerLink="/accounts">Accounts</a>
            </li>
            <li class="nav-item" *ngIf="isLoggedIn">
              <a class="nav-link" routerLink="/chat">Chat</a>
            </li>
            <li class="nav-item" *ngIf="isLoggedIn">
              <a class="nav-link" href="javascript:void(0)" (click)="logout()">Logout</a>
            </li>
            <li class="nav-item" *ngIf="!isLoggedIn">
              <a class="nav-link" routerLink="/auth/login">Login</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  `,
  styles: []
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn();
  }

  logout() {
    this.authService.logout();
  }
}
