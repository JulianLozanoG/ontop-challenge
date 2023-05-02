package controller;


import dto.TransactionDto;
import mapper.TransactionMapper;
import model.Account;
import model.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.AccountService;
import service.TransactionService;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionController transactionController;

    private Account testAccount;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testAccount = new Account();
        testAccount.setId("123");
        testAccount.setBalance(new BigDecimal(500));
    }

    @Test
    void testWithdrawMoney() throws AccountNotFoundException {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNumber("123");
        transactionDto.setAmount(new BigDecimal(100));
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setAmount(new BigDecimal(100));
        transaction.setId("abc123");
        when(accountService.findByAccountNumber("123")).thenReturn(testAccount);
        when(transactionService.createTransaction(any(TransactionDto.class))).thenReturn(transaction);
        when(transactionMapper.toDto(transaction)).thenReturn(transactionDto);
        ResponseEntity<TransactionDto> response = transactionController.withdrawMoney(transactionDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transactionDto, response.getBody());
    }

    @Test
    void testGetTransactions() {
        String accountId = "123";
        BigDecimal minAmount = new BigDecimal(100);
        BigDecimal maxAmount = new BigDecimal(200);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, 2, 1, 0, 0);
        int page = 0;
        int size = 10;
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction();
        transaction1.setAccountId(accountId);
        transaction1.setAmount(new BigDecimal(150));
        transaction1.setId("abc123");
        transactions.add(transaction1);
        Transaction transaction2 = new Transaction();
        transaction2.setAccountId(accountId);
        transaction2.setAmount(new BigDecimal(175));
        transaction2.setId("def456");
        transactions.add(transaction2);
        List<TransactionDto> expected = new ArrayList<>();
        expected.add(new TransactionDto(transaction1));
        expected.add(new TransactionDto(transaction2));

        when(transactionService.getTransactions(accountId, minAmount, maxAmount, startDate, endDate, page, size)).thenReturn(transactions);
        when(transactionMapper.toDto(transaction1)).thenReturn(expected.get(0));
        when(transactionMapper.toDto(transaction2)).thenReturn(expected.get(1));
        List<TransactionDto> actual = transactionController.getTransactions(accountId, minAmount, maxAmount, startDate, endDate, page, size);
        assertEquals(expected, actual);
    }

}