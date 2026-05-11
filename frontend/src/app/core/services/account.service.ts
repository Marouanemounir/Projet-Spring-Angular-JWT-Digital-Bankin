import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BankAccount {
  id: string;
  balance: number;
  createdAt: Date;
  status: string;
  currency: string;
  customerDTO: any;
  type: string;
  overDraft?: number;
  interestRate?: number;
}

export interface AccountOperation {
  id: number;
  operationDate: Date;
  amount: number;
  type: string;
  description: string;
}

export interface AccountHistory {
  accountId: string;
  balance: number;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  accountOperationDTOS: AccountOperation[];
}

export interface AccountPage {
  content: BankAccount[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = 'http://localhost:8085/api/accounts';

  constructor(private http: HttpClient) {}

  getAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(this.apiUrl);
  }

  searchAccounts(keyword: string, page: number, size: number): Observable<AccountPage> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<AccountPage>(`${this.apiUrl}/search`, { params });
  }

  getAccount(id: string): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.apiUrl}/${id}`);
  }

  getAccountOperations(id: string, page: number, size: number): Observable<AccountHistory> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<AccountHistory>(`${this.apiUrl}/${id}/pageOperations`, { params });
  }

  debit(accountId: string, amount: number, description: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/debit`, { accountId, amount, description });
  }

  credit(accountId: string, amount: number, description: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/credit`, { accountId, amount, description });
  }

  transfer(accountSource: string, accountDestination: string, amount: number, description: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/transfer`, { 
      accountIdSource: accountSource, 
      accountIdDestination: accountDestination, 
      amount, 
      description 
    });
  }
}
