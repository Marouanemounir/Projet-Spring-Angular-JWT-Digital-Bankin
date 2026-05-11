import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService, Customer, CustomerPage } from '../../core/services/customer.service';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <div class="card shadow-sm">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
          <h4 class="mb-0">Customers Management</h4>
          <!-- Future: Add Customer button could go here -->
        </div>
        <div class="card-body">
          <div class="row mb-3">
            <div class="col-md-6">
              <form (ngSubmit)="onSearch()" class="d-flex">
                <input type="text" class="form-control me-2" placeholder="Search by name" [(ngModel)]="keyword" name="keyword">
                <button type="submit" class="btn btn-outline-primary">
                  <i class="bi bi-search"></i> Search
                </button>
              </form>
            </div>
          </div>

          <div *ngIf="loading" class="text-center my-4">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">
            {{ errorMessage }}
          </div>

          <div *ngIf="!loading && customers.length > 0">
            <div class="table-responsive">
              <table class="table table-hover table-striped border">
                <thead class="table-light">
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let c of customers">
                    <td>{{ c.id }}</td>
                    <td>{{ c.name }}</td>
                    <td>{{ c.email }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Pagination -->
            <nav aria-label="Page navigation" class="mt-3" *ngIf="totalPages > 1">
              <ul class="pagination justify-content-end">
                <li class="page-item" [class.disabled]="currentPage === 0">
                  <a class="page-link" href="javascript:void(0)" (click)="goToPage(currentPage - 1)">Previous</a>
                </li>
                
                <li class="page-item" *ngFor="let page of getPages()" [class.active]="page === currentPage">
                  <a class="page-link" href="javascript:void(0)" (click)="goToPage(page)">{{ page + 1 }}</a>
                </li>
                
                <li class="page-item" [class.disabled]="currentPage === totalPages - 1">
                  <a class="page-link" href="javascript:void(0)" (click)="goToPage(currentPage + 1)">Next</a>
                </li>
              </ul>
            </nav>
          </div>
          
          <div *ngIf="!loading && customers.length === 0 && !errorMessage" class="alert alert-info">
            No customers found.
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  keyword: string = '';
  currentPage: number = 0;
  pageSize: number = 5;
  totalPages: number = 0;
  
  loading: boolean = false;
  errorMessage: string = '';

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.handleSearchCustomers();
  }

  onSearch(): void {
    this.currentPage = 0;
    this.handleSearchCustomers();
  }

  handleSearchCustomers(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.customerService.searchCustomers(this.keyword, this.currentPage, this.pageSize)
      .subscribe({
        next: (data: CustomerPage) => {
          this.customers = data.content;
          this.totalPages = data.totalPages;
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = "Failed to load customers. " + (err.message || '');
          this.loading = false;
        }
      });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.handleSearchCustomers();
    }
  }

  getPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
