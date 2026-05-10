package ma.abdelali.digitalbanking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {
    
    @NotBlank(message = "Source account ID is required")
    private String accountIdSource;
    
    @NotBlank(message = "Destination account ID is required")
    private String accountIdDestination;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private String description;
}
