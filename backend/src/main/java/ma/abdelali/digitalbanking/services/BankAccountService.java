package ma.abdelali.digitalbanking.services;

import ma.abdelali.digitalbanking.dtos.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface BankAccountService {

    // Customer operations
    CustomerDTO saveCustomer(CreateCustomerRequest request);
    CustomerDTO updateCustomer(Long customerId, UpdateCustomerRequest request);
    void deleteCustomer(Long customerId);
    CustomerDTO getCustomer(Long customerId);
    List<CustomerDTO> listCustomers();
    Page<CustomerDTO> searchCustomers(String keyword, int page, int size);

    // Account operations
    CurrentBankAccountDTO saveCurrentBankAccount(BigDecimal initialBalance, BigDecimal overDraft, Long customerId, String currency);
    SavingBankAccountDTO saveSavingBankAccount(BigDecimal initialBalance, BigDecimal interestRate, Long customerId, String currency);
    BankAccountDTO getBankAccount(String accountId);
    List<BankAccountDTO> bankAccountList();
    Page<BankAccountDTO> searchAccounts(String keyword, int page, int size);
    List<BankAccountDTO> getCustomerAccounts(Long customerId);

    // Transaction operations
    void debit(String accountId, BigDecimal amount, String description);
    void credit(String accountId, BigDecimal amount, String description);
    void transfer(String accountIdSource, String accountIdDestination, BigDecimal amount, String description);

    // History operations
    List<AccountOperationDTO> accountHistory(String accountId);
    AccountHistoryDTO getAccountHistory(String accountId, int page, int size);

    // Dashboard operations
    DashboardStatsDTO getDashboardStats();
    List<MonthlyOperationStatsDTO> getMonthlyOperationStats();
    List<AccountTypeStatsDTO> getAccountTypeStats();
    List<AccountStatusStatsDTO> getAccountStatusStats();
}
