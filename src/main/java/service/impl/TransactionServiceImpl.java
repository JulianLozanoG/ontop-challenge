package service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TransactionDto;
import event.KafkaProducer;
import exception.InsufficientBalanceException;
import exception.NotFoundException;
import model.Account;
import model.transaction.Transaction;
import model.transaction.TransactionEvent;
import model.transaction.TransactionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;
import repository.TransactionRepository;
import service.TransactionService;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.transactions}")
    private String kafkaTopicTransactions;

    @Value("${kafka.topic.transaction}")
    private String transactionTopic;

    public TransactionServiceImpl(AccountRepository accountRepository,
                                  TransactionRepository transactionRepository,
                                  KafkaTemplate<Object, String> kafkaTemplate,
                                  MongoTemplate mongoTemplate,
                                  ObjectMapper objectMapper, KafkaProducer kafkaProducer) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionDto transactionDto) throws InsufficientBalanceException, AccountNotFoundException {
        Account account = accountRepository.findById(transactionDto.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getBalance().compareTo(transactionDto.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(transactionDto.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction(transactionDto.getId(), account.getId(), transactionDto.getAccountNumber(), transactionDto.getDescription(),
                transactionDto.getAmount(), transactionDto.getFee(), LocalDateTime.now(), TransactionStatus.CREATED, account.getBalance(), transactionDto.getTotalAmount());


        transactionRepository.save(transaction);

        TransactionEvent transactionEvent = new TransactionEvent(transaction.getId(), account.getId(), transaction.getAmount(), transaction.getFee(), transaction.getNewBalance(),
                transaction.getTotalAmount(), transaction.getCreationDate(), transaction.getStatus());

        String transactionEventJson;
        try {
            transactionEventJson = objectMapper.writeValueAsString(transactionEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing transaction event to JSON", e);
        }
        kafkaTemplate.send(kafkaTopicTransactions, transactionEventJson);
        return transaction;
    }

    @Override
    public Transaction withdrawMoney(String accountId, BigDecimal amount) {
        // Retrieve account by ID
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        // Calculate transaction fee and total amount
        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.1));
        BigDecimal totalAmount = amount.add(fee);

        // Check if account has sufficient balance
        if (account.getBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // Update account balance
        account.setBalance(account.getBalance().subtract(totalAmount));
        accountRepository.save(account);

        // Create new transaction
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setTotalAmount(totalAmount);
        transaction.setCreationDate(LocalDateTime.now());
            transaction.setStatus(TransactionStatus.COMPLETED);
        transaction = transactionRepository.save(transaction);

        // Publish transaction event to Kafka
        TransactionEvent transactionEvent = TransactionEvent.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .totalAmount(transaction.getTotalAmount())
                .creationDate(transaction.getCreationDate())
                .status(transaction.getStatus())
                .build();

        String transactionEventJson;
        try {
            transactionEventJson = objectMapper.writeValueAsString(transactionEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing transaction event to JSON", e);
        }
        kafkaTemplate.send(kafkaTopicTransactions, transactionEventJson);

        return transaction;
    }

    @Override
    public List<Transaction> getTransactions(String accountId, BigDecimal minAmount, BigDecimal maxAmount,
                                             LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        // Create criteria query for filtering
        Criteria criteria = new Criteria();
        criteria = criteria.and("accountId").is(accountId);
        if (minAmount != null) {
            criteria = criteria.and("totalAmount").gte(minAmount);
        }
        if (maxAmount != null) {
            criteria = criteria.and("totalAmount").lte(maxAmount);
        }
        if (startDate != null) {
            criteria = criteria.and("creationDate").gte(startDate);
        }
        if (endDate != null) {
            criteria = criteria.and("creationDate").lte(endDate);
        }

        // Create sort query for ordering
        Sort sort = Sort.by(Sort.Order.desc("creationDate"));

        // Create pagination request
        Pageable pageable = PageRequest.of(page, size, sort);

        // Execute query and return results
        Query query = Query.query(criteria).with(pageable);
        List<Transaction> transactions = mongoTemplate.find(query, Transaction.class);
        return transactions;
    }
}