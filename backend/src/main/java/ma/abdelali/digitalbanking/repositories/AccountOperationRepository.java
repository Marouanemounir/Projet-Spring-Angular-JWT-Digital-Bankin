package ma.abdelali.digitalbanking.repositories;

import ma.abdelali.digitalbanking.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {

    List<AccountOperation> findByBankAccountId(String accountId);

    Page<AccountOperation> findByBankAccountId(String accountId, Pageable pageable);

    @Query("SELECT SUM(ao.amount) FROM AccountOperation ao WHERE ao.bankAccount.id = :accountId AND ao.type = 'CREDIT'")
    BigDecimal sumCreditByAccountId(@Param("accountId") String accountId);

    @Query("SELECT SUM(ao.amount) FROM AccountOperation ao WHERE ao.bankAccount.id = :accountId AND ao.type = 'DEBIT'")
    BigDecimal sumDebitByAccountId(@Param("accountId") String accountId);

    @Query("SELECT COUNT(ao) FROM AccountOperation ao WHERE ao.bankAccount.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT SUM(ao.amount) FROM AccountOperation ao WHERE ao.type = 'CREDIT'")
    BigDecimal totalCreditAmount();

    @Query("SELECT SUM(ao.amount) FROM AccountOperation ao WHERE ao.type = 'DEBIT'")
    BigDecimal totalDebitAmount();

    @Query("SELECT COUNT(ao) FROM AccountOperation ao")
    long totalOperations();

    @Query("SELECT ao FROM AccountOperation ao WHERE ao.operationDate >= :startDate AND ao.operationDate <= :endDate ORDER BY ao.operationDate DESC")
    List<AccountOperation> findOperationsBetweenDates(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
