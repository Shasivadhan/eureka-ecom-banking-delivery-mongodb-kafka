package com.app.bank.repo;

import com.app.bank.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepo extends MongoRepository<Transaction, String> {

    // Custom query derived from field names
    List<Transaction> findByAccountNumberAndTimestampBetween(
            String accountNumber,
            LocalDateTime start,
            LocalDateTime end
    );
}
