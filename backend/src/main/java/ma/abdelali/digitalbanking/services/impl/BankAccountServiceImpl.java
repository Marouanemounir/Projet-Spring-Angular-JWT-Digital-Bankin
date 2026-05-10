package ma.abdelali.digitalbanking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.abdelali.digitalbanking.dtos.*;
import ma.abdelali.digitalbanking.entities.*;
import ma.abdelali.digitalbanking.enums.AccountStatus;
import ma.abdelali.digitalbanking.enums.OperationType;
import ma.abdelali.digitalbanking.exceptions.*;
import ma.abdelali.digitalbanking.repositories.*;
import ma.abdelali.digitalbanking.services.BankAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    // ==================== Customer Operations ====================

    @Override
    public CustomerDTO saveCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new InvalidOperationException("Email already exists: " + request.getEmail());
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setCreatedBy(getCurrentUsername());

        Customer saved = customerRepository.save(customer);
        log.info("Customer created: {}", saved.getId());
        return mapCustomerToDTO(saved);
    }

    @Override
    public CustomerDTO updateCustomer(Long customerId, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        // Check if new email is already used by another customer
        if (!customer.getEmail().equals(request.getEmail()) && 
            customerRepository.existsByEmail(request.getEmail())) {
            throw new InvalidOperationException("Email already exists: " + request.getEmail());
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setUpdatedBy(getCurrentUsername());

        Customer updated = customerRepository.save(customer);
        log.info("Customer updated: {}", customerId);
        return mapCustomerToDTO(updated);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        if (!customer.getBankAccounts().isEmpty()) {
            throw new InvalidOperationException("Cannot delete customer with active accounts");
        }

        customerRepository.delete(customer);
        log.info("Customer deleted: {}", customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return mapCustomerToDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapCustomerToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchCustomers(String keyword, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<Customer> customers = customerRepository.searchByNameOrEmail(keyword, pageable);
        return customers.map(this::mapCustomerToDTO);
    }

    // ==================== Account Operations ====================

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(BigDecimal initialBalance, BigDecimal overDraft, 
                                                         Long customerId, String currency) {
        if (initialBalance == null || initialBalance.signum() < 0) {
            throw new InvalidOperationException("Initial balance must be positive or zero");
        }
        if (overDraft == null || overDraft.signum() < 0) {
            throw new InvalidOperationException("Overdraft must be positive or zero");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        CurrentAccount account = new CurrentAccount();
        account.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);
        account.setOverDraft(overDraft);
        account.setCustomer(customer);
        account.setStatus(AccountStatus.CREATED);
        account.setCurrency(currency != null ? currency : "MAD");
        account.setCreatedBy(getCurrentUsername());

        CurrentAccount saved = currentAccountRepository.save(account);
        log.info("Current account created: {}", saved.getId());
        return mapCurrentAccountToDTO(saved);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(BigDecimal initialBalance, BigDecimal interestRate, 
                                                       Long customerId, String currency) {
        if (initialBalance == null || initialBalance.signum() < 0) {
            throw new InvalidOperationException("Initial balance must be positive or zero");
        }
        if (interestRate == null || interestRate.signum() < 0) {
            throw new InvalidOperationException("Interest rate must be positive or zero");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        SavingAccount account = new SavingAccount();
        account.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);
        account.setInterestRate(interestRate);
        account.setCustomer(customer);
        account.setStatus(AccountStatus.CREATED);
        account.setCurrency(currency != null ? currency : "MAD");
        account.setCreatedBy(getCurrentUsername());

        SavingAccount saved = savingAccountRepository.save(account);
        log.info("Saving account created: {}", saved.getId());
        return mapSavingAccountToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountDTO getBankAccount(String accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new BankAccountNotFoundException(accountId));
        
        if (account instanceof CurrentAccount) {
            return mapCurrentAccountToDTO((CurrentAccount) account);
        } else if (account instanceof SavingAccount) {
            return mapSavingAccountToDTO((SavingAccount) account);
        }
        return mapBankAccountToDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll().stream()
                .map(account -> {
                    if (account instanceof CurrentAccount) {
                        return mapCurrentAccountToDTO((CurrentAccount) account);
                    } else if (account instanceof SavingAccount) {
                        return mapSavingAccountToDTO((SavingAccount) account);
                    }
                    return mapBankAccountToDTO(account);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountDTO> searchAccounts(String keyword, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<BankAccount> accounts = bankAccountRepository.search(keyword, pageable);
        return accounts.map(account -> {
            if (account instanceof CurrentAccount) {
                return mapCurrentAccountToDTO((CurrentAccount) account);
            } else if (account instanceof SavingAccount) {
                return mapSavingAccountToDTO((SavingAccount) account);
            }
            return mapBankAccountToDTO(account);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDTO> getCustomerAccounts(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        return customer.getBankAccounts().stream()
                .map(account -> {
                    if (account instanceof CurrentAccount) {
                        return mapCurrentAccountToDTO((CurrentAccount) account);
                    } else if (account instanceof SavingAccount) {
                        return mapSavingAccountToDTO((SavingAccount) account);
                    }
                    return mapBankAccountToDTO(account);
                })
                .collect(Collectors.toList());
    }

    // ==================== Transaction Operations ====================

    @Override
    public void debit(String accountId, BigDecimal amount, String description) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidOperationException("Debit amount must be positive");
        }

        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(accountId));

        // Check if account is active
        if (!account.getStatus().equals(AccountStatus.ACTIVATED)) {
            throw new InvalidOperationException("Account is not activated");
        }

        // Check balance
        if (account instanceof SavingAccount) {
            if (account.getBalance().subtract(amount).signum() < 0) {
                throw new BalanceNotSufficientException("Insufficient balance for saving account");
            }
        } else if (account instanceof CurrentAccount) {
            CurrentAccount currentAccount = (CurrentAccount) account;
            BigDecimal allowedBalance = currentAccount.getBalance().add(currentAccount.getOverDraft());
            if (allowedBalance.subtract(amount).signum() < 0) {
                throw new BalanceNotSufficientException("Insufficient balance including overdraft");
            }
        }

        // Perform debit
        account.setBalance(account.getBalance().subtract(amount));
        account.setUpdatedBy(getCurrentUsername());
        bankAccountRepository.save(account);

        // Record operation
        AccountOperation operation = new AccountOperation();
        operation.setAmount(amount);
        operation.setType(OperationType.DEBIT);
        operation.setDescription(description);
        operation.setBankAccount(account);
        operation.setPerformedBy(getCurrentUsername());
        accountOperationRepository.save(operation);

        log.info("Debit operation: account={}, amount={}", accountId, amount);
    }

    @Override
    public void credit(String accountId, BigDecimal amount, String description) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidOperationException("Credit amount must be positive");
        }

        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(accountId));

        // Check if account is active
        if (!account.getStatus().equals(AccountStatus.ACTIVATED)) {
            throw new InvalidOperationException("Account is not activated");
        }

        // Perform credit
        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedBy(getCurrentUsername());
        bankAccountRepository.save(account);

        // Record operation
        AccountOperation operation = new AccountOperation();
        operation.setAmount(amount);
        operation.setType(OperationType.CREDIT);
        operation.setDescription(description);
        operation.setBankAccount(account);
        operation.setPerformedBy(getCurrentUsername());
        accountOperationRepository.save(operation);

        log.info("Credit operation: account={}, amount={}", accountId, amount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, BigDecimal amount, String description) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidOperationException("Transfer amount must be positive");
        }

        // Validate accounts exist
        BankAccount sourceAccount = bankAccountRepository.findById(accountIdSource)
                .orElseThrow(() -> new BankAccountNotFoundException(accountIdSource));
        BankAccount destinationAccount = bankAccountRepository.findById(accountIdDestination)
                .orElseThrow(() -> new BankAccountNotFoundException(accountIdDestination));

        // Check if both accounts are activated
        if (!sourceAccount.getStatus().equals(AccountStatus.ACTIVATED) ||
            !destinationAccount.getStatus().equals(AccountStatus.ACTIVATED)) {
            throw new InvalidOperationException("Both accounts must be activated for transfer");
        }

        // Check balance
        if (sourceAccount instanceof SavingAccount) {
            if (sourceAccount.getBalance().subtract(amount).signum() < 0) {
                throw new BalanceNotSufficientException("Insufficient balance in source saving account");
            }
        } else if (sourceAccount instanceof CurrentAccount) {
            CurrentAccount currentAccount = (CurrentAccount) sourceAccount;
            BigDecimal allowedBalance = currentAccount.getBalance().add(currentAccount.getOverDraft());
            if (allowedBalance.subtract(amount).signum() < 0) {
                throw new BalanceNotSufficientException("Insufficient balance in source account including overdraft");
            }
        }

        // Perform transfer
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));
        sourceAccount.setUpdatedBy(getCurrentUsername());
        destinationAccount.setUpdatedBy(getCurrentUsername());

        bankAccountRepository.save(sourceAccount);
        bankAccountRepository.save(destinationAccount);

        // Record operations
        AccountOperation debitOp = new AccountOperation();
        debitOp.setAmount(amount);
        debitOp.setType(OperationType.DEBIT);
        debitOp.setDescription("Transfer to " + accountIdDestination + (description != null ? " - " + description : ""));
        debitOp.setBankAccount(sourceAccount);
        debitOp.setPerformedBy(getCurrentUsername());
        accountOperationRepository.save(debitOp);

        AccountOperation creditOp = new AccountOperation();
        creditOp.setAmount(amount);
        creditOp.setType(OperationType.CREDIT);
        creditOp.setDescription("Transfer from " + accountIdSource + (description != null ? " - " + description : ""));
        creditOp.setBankAccount(destinationAccount);
        creditOp.setPerformedBy(getCurrentUsername());
        accountOperationRepository.save(creditOp);

        log.info("Transfer operation: from={}, to={}, amount={}", accountIdSource, accountIdDestination, amount);
    }

    // ==================== History Operations ====================

    @Override
    @Transactional(readOnly = true)
    public List<AccountOperationDTO> accountHistory(String accountId) {
        bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(accountId));

        return accountOperationRepository.findByBankAccountId(accountId).stream()
                .map(this::mapOperationToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(accountId));

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<AccountOperation> operations = accountOperationRepository.findByBankAccountId(accountId, pageable);

        return AccountHistoryDTO.builder()
                .accountId(accountId)
                .balance(account.getBalance())
                .currentPage(page)
                .totalPages(operations.getTotalPages())
                .pageSize(size)
                .totalElements(operations.getTotalElements())
                .accountOperationDTOs(operations.getContent().stream()
                        .map(this::mapOperationToDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    // ==================== Dashboard Operations ====================

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        long totalCustomers = customerRepository.count();
        long totalAccounts = bankAccountRepository.count();
        
        BigDecimal totalBalance = bankAccountRepository.findAll().stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditAmount = accountOperationRepository.totalCreditAmount();
        BigDecimal totalDebitAmount = accountOperationRepository.totalDebitAmount();
        long operationsCount = accountOperationRepository.totalOperations();

        return DashboardStatsDTO.builder()
                .totalCustomers(totalCustomers)
                .totalAccounts(totalAccounts)
                .totalCurrentAccounts(bankAccountRepository.countCurrentAccounts())
                .totalSavingAccounts(bankAccountRepository.countSavingAccounts())
                .totalBalance(totalBalance)
                .totalCreditAmount(totalCreditAmount != null ? totalCreditAmount : BigDecimal.ZERO)
                .totalDebitAmount(totalDebitAmount != null ? totalDebitAmount : BigDecimal.ZERO)
                .operationsCount(operationsCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyOperationStatsDTO> getMonthlyOperationStats() {
        // Simplified: return empty list (can be enhanced with actual monthly aggregation)
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountTypeStatsDTO> getAccountTypeStats() {
        return List.of(
            AccountTypeStatsDTO.builder().type("CURRENT").count(bankAccountRepository.countCurrentAccounts()).build(),
            AccountTypeStatsDTO.builder().type("SAVING").count(bankAccountRepository.countSavingAccounts()).build()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountStatusStatsDTO> getAccountStatusStats() {
        return bankAccountRepository.findAll().stream()
                .collect(Collectors.groupingBy(ba -> ba.getStatus().toString(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> AccountStatusStatsDTO.builder().status(e.getKey()).count(e.getValue()).build())
                .collect(Collectors.toList());
    }

    // ==================== Mapping Methods ====================

    private CustomerDTO mapCustomerToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .createdBy(customer.getCreatedBy())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    private BankAccountDTO mapBankAccountToDTO(BankAccount account) {
        return BankAccountDTO.builder()
                .id(account.getId())
                .type(account.getClass().getSimpleName())
                .balance(account.getBalance())
                .status(account.getStatus().toString())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt())
                .createdBy(account.getCreatedBy())
                .customer(mapCustomerToDTO(account.getCustomer()))
                .build();
    }

    private CurrentBankAccountDTO mapCurrentAccountToDTO(CurrentAccount account) {
        CurrentBankAccountDTO dto = new CurrentBankAccountDTO();
        dto.setId(account.getId());
        dto.setType("CURRENT");
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus().toString());
        dto.setCurrency(account.getCurrency());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setCreatedBy(account.getCreatedBy());
        dto.setCustomer(mapCustomerToDTO(account.getCustomer()));
        dto.setOverDraft(account.getOverDraft());
        return dto;
    }

    private SavingBankAccountDTO mapSavingAccountToDTO(SavingAccount account) {
        SavingBankAccountDTO dto = new SavingBankAccountDTO();
        dto.setId(account.getId());
        dto.setType("SAVING");
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus().toString());
        dto.setCurrency(account.getCurrency());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setCreatedBy(account.getCreatedBy());
        dto.setCustomer(mapCustomerToDTO(account.getCustomer()));
        dto.setInterestRate(account.getInterestRate());
        return dto;
    }

    private AccountOperationDTO mapOperationToDTO(AccountOperation operation) {
        return AccountOperationDTO.builder()
                .id(operation.getId())
                .operationDate(operation.getOperationDate())
                .amount(operation.getAmount())
                .type(operation.getType().toString())
                .description(operation.getDescription())
                .performedBy(operation.getPerformedBy())
                .build();
    }

    private String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "SYSTEM";
    }
}
