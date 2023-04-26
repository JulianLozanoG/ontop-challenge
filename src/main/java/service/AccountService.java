package service;

import model.Account;

import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);
    Optional<Account> findByAccountNumber(String accountNumber);
}
