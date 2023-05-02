package model.transaction;

import com.fasterxml.jackson.core.JsonToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TransactionEvent {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal newBalance;
    private BigDecimal totalAmount;
    private LocalDateTime creationDate;
    private TransactionStatus status;
}
