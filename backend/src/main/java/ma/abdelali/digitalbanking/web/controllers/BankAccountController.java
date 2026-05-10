package ma.abdelali.digitalbanking.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.abdelali.digitalbanking.dtos.*;
import ma.abdelali.digitalbanking.services.BankAccountService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    // Account retrieval endpoints
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<BankAccountDTO>> listAccounts() {
        List<BankAccountDTO> accounts = bankAccountService.bankAccountList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Page<BankAccountDTO>> searchAccounts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BankAccountDTO> accounts = bankAccountService.searchAccounts(keyword, page, size);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<BankAccountDTO> getAccount(@PathVariable String accountId) {
        BankAccountDTO account = bankAccountService.getBankAccount(accountId);
        return ResponseEntity.ok(account);
    }

    // Account creation endpoints
    @PostMapping("/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CurrentBankAccountDTO> createCurrentAccount(
            @Valid @RequestBody CreateCurrentAccountRequest request) {
        CurrentBankAccountDTO account = bankAccountService.saveCurrentBankAccount(
                request.getInitialBalance(),
                request.getOverDraft(),
                request.getCustomerId(),
                request.getCurrency()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PostMapping("/saving")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SavingBankAccountDTO> createSavingAccount(
            @Valid @RequestBody CreateSavingAccountRequest request) {
        SavingBankAccountDTO account = bankAccountService.saveSavingBankAccount(
                request.getInitialBalance(),
                request.getInterestRate(),
                request.getCustomerId(),
                request.getCurrency()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    // Operations endpoints
    @GetMapping("/{accountId}/operations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<AccountOperationDTO>> getAccountOperations(@PathVariable String accountId) {
        List<AccountOperationDTO> operations = bankAccountService.accountHistory(accountId);
        return ResponseEntity.ok(operations);
    }

    @GetMapping("/{accountId}/pageOperations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<AccountHistoryDTO> getAccountOperationsPaginated(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        AccountHistoryDTO history = bankAccountService.getAccountHistory(accountId, page, size);
        return ResponseEntity.ok(history);
    }

    // Transaction endpoints
    @PostMapping("/debit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Void> debit(@Valid @RequestBody DebitRequest request) {
        bankAccountService.debit(request.getAccountId(), request.getAmount(), request.getDescription());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/credit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Void> credit(@Valid @RequestBody CreditRequest request) {
        bankAccountService.credit(request.getAccountId(), request.getAmount(), request.getDescription());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        bankAccountService.transfer(
                request.getAccountIdSource(),
                request.getAccountIdDestination(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.noContent().build();
    }
}
