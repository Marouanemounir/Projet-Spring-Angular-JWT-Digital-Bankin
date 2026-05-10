import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'customers',
    loadChildren: () => import('./features/customers/customers.routes').then(m => m.CUSTOMERS_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'accounts',
    loadChildren: () => import('./features/accounts/accounts.routes').then(m => m.ACCOUNTS_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'chat',
    loadChildren: () => import('./features/chatbot/chatbot.routes').then(m => m.CHATBOT_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
