package ma.abdelali.digitalbanking.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.abdelali.digitalbanking.enums.OperationType;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "account_operations", indexes = {
    @Index(name = "idx_bank_account_id", columnList = "bank_account_id"),
    @Index(name = "idx_operation_date", columnList = "operation_date")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "bankAccount")
public class AccountOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant operationDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(length = 100)
    private String performedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        if (this.operationDate == null) {
            this.operationDate = Instant.now();
        }
    }
}
