import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-body">
              <h3 class="card-title text-center">Register</h3>
              <form (ngSubmit)="onRegister()">
                <div class="mb-3">
                  <label class="form-label">Username</label>
                  <input type="text" class="form-control" [(ngModel)]="user.username" name="username" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Email</label>
                  <input type="email" class="form-control" [(ngModel)]="user.email" name="email" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input type="password" class="form-control" [(ngModel)]="user.password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Register</button>
              </form>
              <p class="mt-3 text-center"><a routerLink="/auth/login">Already have an account? Login</a></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  
  user: RegisterRequest = { username: '', email: '', password: '' };
  loading = false;
  submitted = false;

  onRegister() {
    this.submitted = true;
    this.loading = true;
    this.authService.register(this.user).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err: any) => {
        console.error('Register error:', err);
        this.loading = false;
      }
    });
  }
}
