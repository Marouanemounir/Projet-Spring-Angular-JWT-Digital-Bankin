package ma.abdelali.digitalbanking.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountHistoryDTO {
    private String accountId;
    private BigDecimal balance;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private long totalElements;
    private List<AccountOperationDTO> accountOperationDTOs;
}
