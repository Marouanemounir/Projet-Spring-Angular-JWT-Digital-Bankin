# Digital Banking Application - Knowledge Base

## Getting Started

### What is Digital Banking?
Digital Banking is a comprehensive web application for managing bank customers, accounts, and financial operations. It provides a secure, user-friendly interface for banking professionals to manage customer relationships and perform financial transactions.

### Main Features
- Customer Management
- Bank Account Management (Current and Saving Accounts)
- Financial Transactions (Debit, Credit, Transfer)
- Real-time Dashboard with Statistics
- User Authentication and Authorization
- AI-powered Chatbot for assistance

## Customer Management

### How to Create a New Customer
1. Navigate to the Customers section from the main menu
2. Click the "Add Customer" button
3. Fill in the required information:
   - Customer Name (required)
   - Email Address (required, must be unique)
4. Click "Submit" to create the customer
5. The customer is now created and ready to have bank accounts assigned

### Viewing Customers
- Go to Customers menu to see a list of all customers
- Use the search function to find customers by name or email
- Click on a customer's name to see detailed information including their bank accounts
- Pagination is available for easier navigation through large customer lists

### Updating Customer Information
1. Click on the customer you want to edit
2. Click the "Edit" button
3. Update the required information
4. Click "Update" to save changes

### Important Notes
- Each customer must have a unique email address
- A customer must not have any active accounts before deletion
- Customer information is audit-logged with timestamps and who made changes

## Bank Accounts

### Types of Bank Accounts

#### Current Account
- Used for frequent transactions
- Has an overdraft facility (can go below zero up to overdraft limit)
- No interest rate
- Great for business and regular operations

#### Saving Account
- Used for savings
- Cannot have a negative balance (no overdraft)
- Earns interest at a specified rate
- Good for building savings

### Creating a Current Account
1. Go to Accounts section → "Create Account"
2. Select "Current Account"
3. Choose a customer from the dropdown
4. Enter initial balance (optional)
5. Set the overdraft limit (e.g., 5000 MAD)
6. Select currency (default: MAD - Moroccan Dirham)
7. Click "Create"

### Creating a Saving Account
1. Go to Accounts section → "Create Account"
2. Select "Saving Account"
3. Choose a customer from the dropdown
4. Enter initial balance (optional)
5. Set the interest rate (as percentage, e.g., 2.5%)
6. Select currency (default: MAD)
7. Click "Create"

### Account Statuses
- **CREATED**: Account is newly created, not yet active
- **ACTIVATED**: Account is active and ready for transactions
- **SUSPENDED**: Account has been suspended and cannot perform transactions

## Financial Transactions

### Debit Operation (Withdrawal)
Reduces the account balance by transferring money out.

**Rules:**
- For Saving Accounts: Balance cannot go negative
- For Current Accounts: Balance can go to -Overdraft (but not below)
- Account must be activated
- Amount must be positive

**How to perform Debit:**
1. Go to Account Details
2. Click "Debit" button
3. Enter amount to withdraw
4. Add description (optional)
5. Click "Confirm"

### Credit Operation (Deposit)
Increases the account balance by transferring money in.

**Rules:**
- Account must be activated
- Amount must be positive
- No balance restrictions

**How to perform Credit:**
1. Go to Account Details
2. Click "Credit" button
3. Enter amount to deposit
4. Add description (optional)
5. Click "Confirm"

### Transfer Operation
Moves money from one account to another account.

**Rules:**
- Both accounts must be activated
- Source account must have sufficient balance (considering overdraft for current accounts)
- Cannot transfer to self
- Amount must be positive

**How to perform Transfer:**
1. Go to Source Account Details
2. Click "Transfer" button
3. Select destination account
4. Enter transfer amount
5. Add description (optional)
6. Click "Confirm"

## Dashboard and Statistics

### Dashboard Overview
The dashboard provides at-a-glance metrics for banking operations:
- **Total Customers**: Count of all registered customers
- **Total Accounts**: Count of all bank accounts (current + saving)
- **Total Balance**: Sum of all account balances across the system
- **Total Credit**: Sum of all credit operations
- **Total Debit**: Sum of all debit operations
- **Operations Count**: Total number of transactions performed

### Charts and Visualizations
- **Account Type Distribution**: Pie chart showing current vs saving accounts
- **Account Status Distribution**: Chart showing account status breakdown
- **Monthly Operations**: Line chart showing transaction volume by month

## User Roles and Permissions

### ADMIN Role
- Full system access
- Can create, update, delete customers
- Can manage accounts
- Can perform all transactions
- Can access dashboard and analytics
- Can manage users and assign roles

### MANAGER Role
- Can create and manage customers
- Can create and manage bank accounts
- Can perform financial transactions
- Can view dashboard
- Cannot manage other users

### USER Role
- Can view their assigned accounts
- Can perform personal account transactions
- Can view operation history
- Limited dashboard access

## Security

### Password Requirements
- Minimum 6 characters
- Passwords are encrypted using BCrypt
- Change password regularly for security
- Never share your password

### How to Change Password
1. Click on your account menu (top right)
2. Select "Change Password"
3. Enter your current password
4. Enter new password (minimum 6 characters)
5. Click "Update"

### Authentication
- Uses JWT (JSON Web Tokens) for secure authentication
- Tokens expire after 24 hours
- Session is stateless for scalability
- All API calls must include valid token

## Account Operations History

### Viewing Transaction History
1. Go to Account Details
2. Scroll down to "Operations" section
3. See all debit and credit transactions
4. Transactions show: date, amount, type, description, and who performed it

### Operation Details
Each operation record contains:
- **Date**: When the transaction occurred
- **Amount**: Transaction amount
- **Type**: DEBIT or CREDIT
- **Description**: Details about the transaction
- **Performed By**: Username of who executed the transaction

### Pagination
- Default: 5 operations per page
- Can navigate through pages
- Recent operations appear first

## Currency and Localization

### Supported Currencies
- MAD (Moroccan Dirham) - Default
- Can create accounts in other currencies if configured

### Amount Precision
- All amounts use BigDecimal for accurate financial calculations
- Decimal places: 2 (e.g., 1000.50)
- No rounding errors

## Troubleshooting

### Common Issues

#### "Account Not Found"
- Verify the account ID is correct
- Check if account has been deleted
- Ensure you have permission to access the account

#### "Insufficient Balance"
- For Saving Accounts: Available balance cannot be negative
- For Current Accounts: Balance cannot exceed overdraft limit
- Try a smaller amount or check account status

#### "Account Not Activated"
- New accounts start in CREATED status
- An admin must activate the account before transactions
- Contact administrator to activate your account

#### "Operation Took Too Long"
- Check internet connection
- Try again after a moment
- Contact support if issue persists

## Best Practices

### For Customers
1. Keep account information updated
2. Monitor account balance regularly
3. Save transaction confirmations
4. Change password regularly
5. Use current accounts for frequent transactions
6. Use saving accounts to build emergency funds

### For Managers
1. Verify customer information before account creation
2. Set appropriate overdraft limits
3. Monitor suspicious transactions
4. Keep audit logs for compliance
5. Regularly review dashboard statistics
6. Update inactive customer records

### For Admins
1. Perform regular backups
2. Monitor user activities
3. Review and revoke unused credentials
4. Maintain role assignments
5. Monitor system performance
6. Keep security patches updated

## FAQ

### Q: Can I delete a customer?
A: Only if the customer has no active accounts. Delete all accounts first.

### Q: What happens if I exceed the overdraft limit?
A: The transaction will be rejected with "Insufficient Balance" error.

### Q: How long are my login sessions valid?
A: 24 hours from login. You'll need to log in again after expiration.

### Q: Can I transfer between accounts of different currencies?
A: No, transfers must be between accounts of the same currency.

### Q: Who can see my account information?
A: Only authorized users (ADMIN, MANAGER) and yourself can view account details.

### Q: What should I do if I forget my password?
A: Contact an administrator to reset your password.

### Q: Can I reverse a transaction?
A: No, transactions are permanent. You can perform an opposite transaction (e.g., credit to reverse a debit).


## System Information

- **Version**: 1.0.0
- **Platform**: Web (Browser-based)
- **Supported Browsers**: Chrome, Firefox, Edge, Safari
- **Database**: MySQL 8.0+
- **Backend**: Spring Boot 3.x

---

