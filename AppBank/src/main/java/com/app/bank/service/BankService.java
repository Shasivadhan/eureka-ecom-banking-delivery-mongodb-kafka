package com.app.bank.service;

import com.app.bank.dto.*;
import java.util.List;

public interface BankService {
    UserResponseDTO registerUser(RegistrationRequest request);
    String transferFunds(FundTransferRequest request);   // ✅ keep for Mongo too
    List<String> getStatement(StatementRequest request);
    String deleteAccountByNumber(String accountNumber);
    String deleteAllAccounts();
    String deposit(DepositRequest request);
    UserResponseDTO getUserByAccountNumber(String accountNumber);
    PublicUserResponseDTO getPublicUserView(String accountNumber);
    List<UserResponseDTO> getAllUsers();
    String debitAmount(DebitRequest request);            // ✅ used by E-Commerce service
}
