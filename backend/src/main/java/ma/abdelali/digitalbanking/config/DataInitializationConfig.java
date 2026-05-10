package ma.abdelali.digitalbanking.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.abdelali.digitalbanking.entities.AppRole;
import ma.abdelali.digitalbanking.entities.AppUser;
import ma.abdelali.digitalbanking.entities.Customer;
import ma.abdelali.digitalbanking.repositories.AppRoleRepository;
import ma.abdelali.digitalbanking.repositories.AppUserRepository;
import ma.abdelali.digitalbanking.repositories.CustomerRepository;
import ma.abdelali.digitalbanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializationConfig {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final BankAccountService bankAccountService;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("========== Initializing Development Data ==========");

            // Initialize roles
            if (appRoleRepository.count() == 0) {
                AppRole adminRole = new AppRole(null, "ROLE_ADMIN", "Administrator role");
                AppRole userRole = new AppRole(null, "ROLE_USER", "User role");
                AppRole managerRole = new AppRole(null, "ROLE_MANAGER", "Manager role");

                appRoleRepository.save(adminRole);
                appRoleRepository.save(userRole);
                appRoleRepository.save(managerRole);
                log.info("✓ Roles created");
            }

            // Initialize admin user
            if (appUserRepository.count() == 0) {
                AppRole adminRole = appRoleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
                
                AppUser adminUser = new AppUser();
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@banking.local");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setEnabled(true);
                
                Set<AppRole> roles = new HashSet<>();
                roles.add(adminRole);
                adminUser.setRoles(roles);

                appUserRepository.save(adminUser);
                log.info("✓ Admin user created (username: admin, password: admin123)");
            }

            // Initialize sample customers
            if (customerRepository.count() == 0) {
                String[] names = {"Hassan Ahmed", "Imane Bennani", "Mohamed Saadi"};
                String[] emails = {"hassan@example.com", "imane@example.com", "mohamed@example.com"};

                for (int i = 0; i < names.length; i++) {
                    Customer customer = new Customer();
                    customer.setName(names[i]);
                    customer.setEmail(emails[i]);
                    customer.setCreatedBy("system");
                    
                    Customer savedCustomer = customerRepository.save(customer);
                    log.info("✓ Customer created: {}", names[i]);

                    // Create one current account and one saving account for each customer
                    try {
                        bankAccountService.saveCurrentBankAccount(
                                new BigDecimal("10000"),
                                new BigDecimal("5000"),
                                savedCustomer.getId(),
                                "MAD"
                        );
                        log.info("  ✓ Current account created for {}", names[i]);

                        bankAccountService.saveSavingBankAccount(
                                new BigDecimal("5000"),
                                new BigDecimal("2.5"),
                                savedCustomer.getId(),
                                "MAD"
                        );
                        log.info("  ✓ Saving account created for {}", names[i]);
                    } catch (Exception e) {
                        log.error("Error creating accounts for customer: {}", names[i], e);
                    }
                }
            }

            log.info("========== Development Data Initialization Complete ==========");
        };
    }
}
