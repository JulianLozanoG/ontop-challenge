package dto;

import lombok.Data;
import model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private String id;
    private LocalDateTime creationDate;
    private BigDecimal amount;
    private BigDecimal fee;
    private TransactionStatus status;
}