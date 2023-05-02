package model.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;
    private String accountId;
    private String accountNumber;
    private String description;
    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDateTime creationDate;
    private TransactionStatus status;
    private BigDecimal newBalance;
    private BigDecimal totalAmount;
}
