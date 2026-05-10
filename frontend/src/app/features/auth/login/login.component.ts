import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-body">
              <h3 class="card-title text-center">Login</h3>
              <form (ngSubmit)="onLogin()">
                <div class="mb-3">
                  <label class="form-label">Username</label>
                  <input type="text" class="form-control" [(ngModel)]="credentials.username" name="username" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input type="password" class="form-control" [(ngModel)]="credentials.password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Login</button>
              </form>
              <p class="mt-3 text-center"><a routerLink="/auth/register">Don't have an account? Register</a></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  
  credentials: LoginRequest = { username: '', password: '' };
  loading = false;
  submitted = false;

  onLogin() {
    this.submitted = true;
    this.loading = true;
    this.authService.login(this.credentials).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err: any) => {
        console.error('Login error:', err);
        this.loading = false;
      }
    });
  }
}
