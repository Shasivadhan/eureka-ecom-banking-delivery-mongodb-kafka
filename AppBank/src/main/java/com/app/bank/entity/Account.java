package com.app.bank.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.util.List;

@Document(collection = "accounts")   // MongoDB collection name
public class Account {

    @Id
    private String accountNumber;   // This will be the document's _id

    private BigDecimal balance;

    private String fullName;

    // Instead of @OneToMany, MongoDB just stores embedded documents or references.
    // We'll store transactions directly as a list.
    private List<Transaction> transactions;

    // --- Getters and Setters ---
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
