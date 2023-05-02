package model.transaction;

import java.time.LocalDateTime;

public enum TransactionStatus {
    CREATED,
    PENDING,
    SUCCESSFUL,
    COMPLETED,
    FAILED,
    SUCCESS, APPROVED;
}
