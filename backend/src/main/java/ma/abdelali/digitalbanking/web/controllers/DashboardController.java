package ma.abdelali.digitalbanking.web.controllers;

import lombok.RequiredArgsConstructor;
import ma.abdelali.digitalbanking.dtos.*;
import ma.abdelali.digitalbanking.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final BankAccountService bankAccountService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        DashboardStatsDTO stats = bankAccountService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/monthly-operations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MonthlyOperationStatsDTO>> getMonthlyOperations() {
        List<MonthlyOperationStatsDTO> stats = bankAccountService.getMonthlyOperationStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/account-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<AccountTypeStatsDTO>> getAccountTypes() {
        List<AccountTypeStatsDTO> stats = bankAccountService.getAccountTypeStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/account-statuses")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<AccountStatusStatsDTO>> getAccountStatuses() {
        List<AccountStatusStatsDTO> stats = bankAccountService.getAccountStatusStats();
        return ResponseEntity.ok(stats);
    }
}
