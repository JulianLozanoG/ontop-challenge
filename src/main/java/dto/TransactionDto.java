package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.transaction.Transaction;
import model.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

    private String id;
    private String accountNumber;
    private String description;
    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDateTime creationDate;
    private TransactionStatus status;
    private BigDecimal totalAmount;

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.accountNumber = transaction.getAccountNumber();
        this.totalAmount = transaction.getTotalAmount();
        this.creationDate = transaction.getCreationDate();
    }
}