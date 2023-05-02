package service.impl;

import exception.NotFoundException;
import model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;
import service.AccountService;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account findById(String id) throws NotFoundException {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));
    }

    @Override
    public Account findByAccountNumber(String accountNumber) throws NotFoundException {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) throws NotFoundException {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException("Account not found with account number: " + accountNumber);
        }
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account withdraw(String accountNumber, BigDecimal amount) throws NotFoundException {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException("Account not found with account number: " + accountNumber);
        }
        BigDecimal newBalance = account.getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Not enough balance for account number: " + accountNumber);
        }
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
}