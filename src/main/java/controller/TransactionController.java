package controller;

import dto.TransactionDto;
import jakarta.validation.Valid;
import mapper.TransactionMapper;
import model.transaction.Transaction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AccountService;
import service.TransactionService;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, AccountService accountService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> withdrawMoney(@RequestBody @Valid TransactionDto transactionDto) throws AccountNotFoundException {
        Transaction transaction = transactionService.createTransaction(transactionDto);
        TransactionDto newTransactionDto = transactionMapper.toDto(transaction);
        return ResponseEntity.created(URI.create("/transactions/" + transaction.getId()))
                .body(newTransactionDto);
    }

    @GetMapping
    public List<TransactionDto> getTransactions(@PathVariable String accountId,
                                                @RequestParam(required = false) BigDecimal minAmount,
                                                @RequestParam(required = false) BigDecimal maxAmount,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        List<Transaction> transactions = transactionService.getTransactions(accountId, minAmount, maxAmount, startDate, endDate, page, size);
        return transactions.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
}

