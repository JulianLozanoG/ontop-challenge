package repository;

import model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByAccountNumber(String accountNumber, Pageable pageable);

    @Query("{ accountNumber: ?0, amount: { $gte: ?1, $lte: ?2 }, creationDate: { $gte: ?3, $lte: ?4 } }")
    List<Transaction> findByAmountAndDate(String accountNumber, Double minAmount, Double maxAmount, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

}
