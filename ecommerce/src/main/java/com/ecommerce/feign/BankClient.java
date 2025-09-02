package com.ecommerce.feign;


import com.ecommerce.dto.DebitRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bank-service", url = "http://localhost:8082/banking")
public interface BankClient {

    @PostMapping("/api/account/debit")
    String debitAccount(@RequestBody DebitRequest request);
}
