package service.impl;

import lombok.RequiredArgsConstructor;
import model.Account;
import model.Transaction;
import model.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;
import repository.TransactionRepository;
import service.AccountService;
import service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final String transactionTopic;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountService accountService,
                                  KafkaTemplate<String, Transaction> kafkaTemplate,
                                  @Value("${kafka.topic.transaction}") String transactionTopic) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.kafkaTemplate = kafkaTemplate;
        this.transactionTopic = transactionTopic;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Account account = accountService.getAccountById(transaction.getAccountId());

        if (account == null) {
            throw new EntityNotFoundException("Account not found with id: " + transaction.getAccountId());
        }

        BigDecimal balance = account.getBalance();
        BigDecimal amount = transaction.getAmount();
        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.1));
        BigDecimal totalAmount = amount.add(fee);

        if (balance.compareTo(totalAmount) < 0) {
            throw new InsufficientFundsException("Insufficient funds to withdraw amount: " + amount);
        }

        account.setBalance(balance.subtract(totalAmount));
        accountService.saveAccount(account);

        transaction.setFee(fee);
        transaction.setStatus(TransactionStatus.SUCCESS);
        Transaction savedTransaction = transactionRepository.save(transaction);

        kafkaTemplate.send(transactionTopic, savedTransaction);
        return savedTransaction;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(String accountId, BigDecimal amountFrom, BigDecimal amountTo,
                                                        LocalDate dateFrom, LocalDate dateTo, Pageable pageable) {
        return transactionRepository.findByAccountIdAndAmountAndDate(accountId, amountFrom, amountTo, dateFrom, dateTo, pageable);
    }

    @Override
    public List<Transaction> getTransactionsByAmountAndDate(BigDecimal amountFrom, BigDecimal amountTo,
                                                            LocalDate dateFrom, LocalDate dateTo, Pageable pageable) {
        return transactionRepository.findByAmountAndDate(amountFrom, amountTo, dateFrom, dateTo, pageable);
    }
} []