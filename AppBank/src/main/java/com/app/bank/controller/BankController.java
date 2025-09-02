package com.app.bank.controller;

import com.app.bank.dto.*;
import com.app.bank.entity.User;
import com.app.bank.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Banking Account Services Portal")
public class BankController {

    @Autowired
    private BankService bankService;

    @PostMapping("/user/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody RegistrationRequest request) {
        UserResponseDTO response = bankService.registerUser(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/funds/transfer")
    public String transferFunds(@RequestBody FundTransferRequest request) {
        return bankService.transferFunds(request);
    }

    @PostMapping("/get/account/statement")
    public List<String> getStatement(@RequestBody StatementRequest request) {
        return bankService.getStatement(request);
    }

    @DeleteMapping("/delete/by/account/{accountNumber}")
    public String deleteAccount(@PathVariable String accountNumber) {
        return bankService.deleteAccountByNumber(accountNumber);
    }

    @DeleteMapping("/delete/allAccounts")
    public String deleteAllAccounts() {
        return bankService.deleteAllAccounts();
    }

    @PostMapping("/amount/deposit")
    public String deposit(@RequestBody DepositRequest request) {
        return bankService.deposit(request);
    }

    @GetMapping("/get/all/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(bankService.getAllUsers());
    }


    @GetMapping("/get/account/details/{accountNumber}")
    public ResponseEntity<PublicUserResponseDTO> getPublicAccountDetails(@PathVariable String accountNumber) {
        return ResponseEntity.ok(bankService.getPublicUserView(accountNumber));
    }

    @PostMapping("/account/debit")
    public ResponseEntity<String> debit(@RequestBody DebitRequest request) {
        return ResponseEntity.ok(bankService.debitAmount(request));
    }


}
