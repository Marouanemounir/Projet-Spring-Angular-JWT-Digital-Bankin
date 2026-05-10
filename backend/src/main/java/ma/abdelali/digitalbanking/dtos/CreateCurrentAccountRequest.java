package ma.abdelali.digitalbanking.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCurrentAccountRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Initial balance must be positive or zero")
    private BigDecimal initialBalance;
    
    @NotNull(message = "Overdraft is required")
    @PositiveOrZero(message = "Overdraft must be positive or zero")
    private BigDecimal overDraft;
    
    @Builder.Default
    private String currency = "MAD";
}
