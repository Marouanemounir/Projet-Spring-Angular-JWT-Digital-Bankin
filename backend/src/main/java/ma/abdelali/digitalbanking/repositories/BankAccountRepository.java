package ma.abdelali.digitalbanking.repositories;

import ma.abdelali.digitalbanking.entities.BankAccount;
import ma.abdelali.digitalbanking.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    List<BankAccount> findByCustomerId(Long customerId);

    List<BankAccount> findByStatus(AccountStatus status);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.customer.id = :customerId")
    List<BankAccount> findAccountsByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT ba FROM BankAccount ba WHERE LOWER(ba.id) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(ba.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<BankAccount> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT ba.account_type) as type FROM BankAccount ba WHERE ba.account_type = 'CA'")
    long countCurrentAccounts();

    @Query("SELECT COUNT(DISTINCT ba.account_type) as type FROM BankAccount ba WHERE ba.account_type = 'SA'")
    long countSavingAccounts();
}
