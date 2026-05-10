package ma.abdelali.digitalbanking.dtos;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsDTO {
    private long totalCustomers;
    private long totalAccounts;
    private long totalCurrentAccounts;
    private long totalSavingAccounts;
    private BigDecimal totalBalance;
    private BigDecimal totalCreditAmount;
    private BigDecimal totalDebitAmount;
    private long operationsCount;
}
