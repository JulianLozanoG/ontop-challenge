package repository;

import model.transaction.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByAccountId(String accountId, Pageable pageable);

    Iterable<Transaction> findByAccountIdAndWithdrawalAmountBetweenAndCreationDateBetween(String accountId, double minAmount, double maxAmount, LocalDateTime startDate, LocalDateTime endDate);
}
