package service;

import dto.TransactionDto;
import exception.InsufficientBalanceException;
import model.transaction.Transaction;
import org.springframework.data.domain.Page;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    Transaction createTransaction(TransactionDto transactionDto) throws InsufficientBalanceException, AccountNotFoundException;
    Transaction withdrawMoney(String accountId, BigDecimal amount);
    List<Transaction> getTransactions(String accountId, BigDecimal minAmount, BigDecimal maxAmount,
                                      LocalDateTime startDate, LocalDateTime endDate, int page, int size);
}