package ma.abdelali.digitalbanking.repositories;

import ma.abdelali.digitalbanking.entities.CurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, String> {
}
