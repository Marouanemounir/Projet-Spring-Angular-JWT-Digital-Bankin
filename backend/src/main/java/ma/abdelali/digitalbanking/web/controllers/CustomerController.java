package ma.abdelali.digitalbanking.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.abdelali.digitalbanking.dtos.CreateCustomerRequest;
import ma.abdelali.digitalbanking.dtos.CustomerDTO;
import ma.abdelali.digitalbanking.dtos.UpdateCustomerRequest;
import ma.abdelali.digitalbanking.services.BankAccountService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    private final BankAccountService bankAccountService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<CustomerDTO>> listCustomers() {
        List<CustomerDTO> customers = bankAccountService.listCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Page<CustomerDTO>> searchCustomers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CustomerDTO> customers = bankAccountService.searchCustomers(keyword, page, size);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        CustomerDTO customer = bankAccountService.getCustomer(id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerDTO customer = bankAccountService.saveCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerDTO customer = bankAccountService.updateCustomer(id, request);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        bankAccountService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/accounts")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<?> getCustomerAccounts(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getCustomerAccounts(id));
    }
}
