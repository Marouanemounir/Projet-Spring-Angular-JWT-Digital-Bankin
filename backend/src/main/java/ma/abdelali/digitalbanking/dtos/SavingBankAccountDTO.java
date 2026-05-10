package ma.abdelali.digitalbanking.dtos;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SavingBankAccountDTO extends BankAccountDTO {
    private BigDecimal interestRate;
}
