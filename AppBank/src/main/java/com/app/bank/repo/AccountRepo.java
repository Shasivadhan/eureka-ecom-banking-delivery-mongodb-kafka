
package com.app.bank.repo;

import com.app.bank.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepo extends MongoRepository<Account, String> {
}
