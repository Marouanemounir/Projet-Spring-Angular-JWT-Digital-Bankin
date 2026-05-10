package ma.abdelali.digitalbanking.repositories;

import ma.abdelali.digitalbanking.entities.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingAccountRepository extends JpaRepository<SavingAccount, String> {
}
