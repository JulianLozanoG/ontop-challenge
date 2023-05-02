package service;

import exception.NotFoundException;
import model.Account;
import model.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AccountService {
    Account create(Account account);
    Account findById(String id) throws NotFoundException;
    Account findByAccountNumber(String accountNumber) throws NotFoundException;
    Account deposit(String accountNumber, BigDecimal amount) throws NotFoundException;
    Account withdraw(String accountNumber, BigDecimal amount) throws NotFoundException;
}