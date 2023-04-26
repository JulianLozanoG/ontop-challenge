package dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    @NotNull
    private String accountId;
    @NotNull
    @Min(1)
    private BigDecimal amount;
}
