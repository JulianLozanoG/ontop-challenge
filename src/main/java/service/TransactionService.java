package service;

import model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);

    List<Transaction> getTransactionsByAccountId(String accountId, BigDecimal amountFrom, BigDecimal amountTo,
                                                 LocalDate dateFrom, LocalDate dateTo, Pageable pageable);

    List<Transaction> getTransactionsByAmountAndDate(BigDecimal amountFrom, BigDecimal amountTo,
                                                     LocalDate dateFrom, LocalDate dateTo, Pageable pageable);
}
