package com.app.bank.dto;

public class PublicUserResponseDTO {

    private String fullName;
    private String accountNumber;
    private double balance;
    private int age;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String panNumber;
    private String aadharNumber;
    private String nationality;

    public PublicUserResponseDTO() {
    }

    public PublicUserResponseDTO(String fullName, String accountNumber, double balance, int age, String gender,
                                 String email, String phone, String address,
                                 String panNumber, String aadharNumber, String nationality) {
        this.fullName = fullName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.panNumber = panNumber;
        this.aadharNumber = aadharNumber;
        this.nationality = nationality;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
