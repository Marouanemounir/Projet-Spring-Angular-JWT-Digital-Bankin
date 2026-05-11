import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService, BankAccount, AccountPage } from '../../core/services/account.service';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <div class="card shadow-sm">
        <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
          <h4 class="mb-0">Bank Accounts Management</h4>
        </div>
        <div class="card-body">
          <div class="row mb-3">
            <div class="col-md-5">
              <form (ngSubmit)="onSearch()" class="d-flex">
                <input type="text" class="form-control me-2" placeholder="Search by account ID" [(ngModel)]="keyword" name="keyword">
                <button type="submit" class="btn btn-outline-success">
                  <i class="bi bi-search"></i> Search
                </button>
              </form>
            </div>
            
            <!-- QUICK TRANSACTION TEST -->
            <div class="col-md-7 border-start pl-3">
              <form (ngSubmit)="testTransaction()" class="d-flex align-items-center">
                <span class="me-2 fw-bold text-muted small">Quick Test:</span>
                <select class="form-select form-select-sm me-2" [(ngModel)]="txType" name="txType" style="width: auto;">
                  <option value="CREDIT">Credit</option>
                  <option value="DEBIT">Debit</option>
                </select>
                <input type="text" class="form-control form-control-sm me-2" placeholder="Account ID" [(ngModel)]="txAccountId" name="txAccountId" style="width: 130px;">
                <input type="number" class="form-control form-control-sm me-2" placeholder="Amount" [(ngModel)]="txAmount" name="txAmount" style="width: 100px;">
                <button type="submit" class="btn btn-sm btn-primary" [disabled]="!txAccountId || !txAmount">Submit</button>
              </form>
            </div>
            <!-- END QUICK TRANSACTION TEST -->
          </div>

          <div *ngIf="loading" class="text-center my-4">
            <div class="spinner-border text-success" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">
            {{ errorMessage }}
          </div>

          <div *ngIf="!loading && accounts.length > 0">
            <div class="table-responsive">
              <table class="table table-hover table-striped border">
                <thead class="table-light">
                  <tr>
                    <th>Account ID</th>
                    <th>Type</th>
                    <th>Balance</th>
                    <th>Currency</th>
                    <th>Status</th>
                    <th>Created At</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let acc of accounts">
                    <td><code>{{ acc.id }}</code></td>
                    <td><span class="badge" [ngClass]="acc.type === 'CurrentAccount' ? 'bg-primary' : 'bg-info'">{{ acc.type }}</span></td>
                    <td class="fw-bold">{{ acc.balance | number:'1.2-2' }}</td>
                    <td>{{ acc.currency }}</td>
                    <td>
                      <span class="badge" [ngClass]="acc.status === 'ACTIVATED' ? 'bg-success' : (acc.status === 'CREATED' ? 'bg-warning text-dark' : 'bg-danger')">
                        {{ acc.status }}
                      </span>
                    </td>
                    <td>{{ acc.createdAt | date:'shortDate' }}</td>
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
          
          <div *ngIf="!loading && accounts.length === 0 && !errorMessage" class="alert alert-info">
            No bank accounts found.
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class AccountsComponent implements OnInit {
  accounts: BankAccount[] = [];
  keyword: string = '';
  currentPage: number = 0;
  pageSize: number = 5;
  totalPages: number = 0;
  
  loading: boolean = false;
  errorMessage: string = '';

  // Quick transaction fields
  txType: string = 'CREDIT';
  txAccountId: string = '';
  txAmount: number | null = null;

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.handleSearchAccounts();
  }

  testTransaction(): void {
    if (!this.txAccountId || !this.txAmount) return;

    const desc = "Test operation via UI";
    let request;

    if (this.txType === 'CREDIT') {
      request = this.accountService.credit(this.txAccountId, this.txAmount, desc);
    } else {
      request = this.accountService.debit(this.txAccountId, this.txAmount, desc);
    }

    request.subscribe({
      next: () => {
        alert(`${this.txType} of ${this.txAmount} successful! Check your Telegram!`);
        this.handleSearchAccounts(); // refresh list
      },
      error: (err) => {
        alert("Transaction failed: " + err.message);
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.handleSearchAccounts();
  }

  handleSearchAccounts(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.accountService.searchAccounts(this.keyword, this.currentPage, this.pageSize)
      .subscribe({
        next: (data: AccountPage) => {
          this.accounts = data.content;
          this.totalPages = data.totalPages;
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = "Failed to load accounts. " + (err.message || '');
          this.loading = false;
        }
      });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.handleSearchAccounts();
    }
  }

  getPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
