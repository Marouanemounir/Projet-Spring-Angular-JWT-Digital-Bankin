import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-4">
      <h1>Dashboard</h1>
      <div class="alert alert-info">Dashboard statistics will be displayed here</div>
      <div *ngIf="stats">
        <div class="row">
          <div class="col-md-3">
            <div class="card">
              <div class="card-body">
                <h5 class="card-title">Total Customers</h5>
                <p class="card-text display-6">{{ stats.totalCustomers }}</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card">
              <div class="card-body">
                <h5 class="card-title">Total Accounts</h5>
                <p class="card-text display-6">{{ stats.totalAccounts }}</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card">
              <div class="card-body">
                <h5 class="card-title">Total Balance</h5>
                <p class="card-text display-6">{{ stats.totalBalance | currency }}</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card">
              <div class="card-body">
                <h5 class="card-title">Total Operations</h5>
                <p class="card-text display-6">{{ stats.operationsCount }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class DashboardComponent implements OnInit {
  stats: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    this.http.get<any>('http://localhost:8085/api/dashboard/stats')
      .subscribe({
        next: (data) => this.stats = data,
        error: (err) => console.error('Error loading stats:', err)
      });
  }
}
