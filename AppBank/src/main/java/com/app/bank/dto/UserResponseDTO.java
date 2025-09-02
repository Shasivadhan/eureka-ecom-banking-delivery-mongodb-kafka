package com.app.bank.dto;

import java.math.BigDecimal;

public class UserResponseDTO {

    private String fullName;
    private String accountNumber;
    private double balance;

    private int age;
    private String gender;

    public UserResponseDTO() {
    }

    public UserResponseDTO(String fullName, String accountNumber, BigDecimal balance) {
        this.fullName = fullName;
        this.accountNumber = accountNumber;
        this.balance = balance.doubleValue();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
