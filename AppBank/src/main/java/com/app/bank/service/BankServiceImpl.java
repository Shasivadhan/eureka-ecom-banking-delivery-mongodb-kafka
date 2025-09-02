package com.app.bank.service;

import com.app.bank.dto.*;
import com.app.bank.entity.*;
import com.app.bank.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    // --- Helper to generate 10-digit account number ---
    private String generateAccountNumber() {
        return String.valueOf(new Random().nextLong(1000000000L, 9999999999L));
    }

    @Override
    public UserResponseDTO registerUser(RegistrationRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setAge(request.getAge());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setPanNumber(request.getPanNumber());
        user.setAadharNumber(request.getAadharNumber());
        user.setNationality(request.getNationality());

        // Create account
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.valueOf(10000)); // default ₹10,000
        account.setFullName(request.getFullName());

        accountRepo.save(account);
        user.setAccount(account);

        userRepo.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setFullName(user.getFullName());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance().doubleValue());
        response.setAge(user.getAge());
        response.setGender(user.getGender());
        return response;
    }

    @Override
    public String transferFunds(FundTransferRequest request) {
        Account from = accountRepo.findById(request.getFromAccount())
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account to = accountRepo.findById(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        BigDecimal amount = request.getAmount();

        if (from.getBalance().subtract(amount).compareTo(BigDecimal.valueOf(5000)) < 0) {
            return "Transfer failed: Minimum ₹5000 balance must remain.";
        }

        // Update balances
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepo.save(from);
        accountRepo.save(to);

        // Record transactions
        Transaction debit = new Transaction();
        debit.setType("DEBIT");
        debit.setAmount(amount);
        debit.setTimestamp(LocalDateTime.now());
        debit.setAccountNumber(from.getAccountNumber());
        debit.setDescription("Transferred to " + to.getAccountNumber());

        Transaction credit = new Transaction();
        credit.setType("CREDIT");
        credit.setAmount(amount);
        credit.setTimestamp(LocalDateTime.now());
        credit.setAccountNumber(to.getAccountNumber());
        credit.setDescription("Received from " + from.getAccountNumber());

        transactionRepo.saveAll(List.of(debit, credit));

        return "Transfer successful.";
    }

    @Override
    public List<String> getStatement(StatementRequest request) {
        YearMonth ym = YearMonth.of(request.getYear(), request.getMonth());
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Transaction> txns = transactionRepo
                .findByAccountNumberAndTimestampBetween(request.getAccountNumber(), start, end);

        List<String> result = new ArrayList<>();
        for (Transaction t : txns) {
            result.add(t.getTimestamp() + " - " + t.getType() + ": ₹" + t.getAmount()
                    + " (" + t.getDescription() + ")");
        }
        return result;
    }

    @Override
    public String deleteAccountByNumber(String accountNumber) {
        Account account = accountRepo.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        User user = userRepo.findByAccount_AccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepo.delete(user);
        accountRepo.delete(account);

        return "Account " + accountNumber + " deleted.";
    }

    @Override
    public String deleteAllAccounts() {
        userRepo.deleteAll();
        accountRepo.deleteAll();
        transactionRepo.deleteAll();
        return "All accounts, users, and transactions deleted.";
    }

    @Override
    public String deposit(DepositRequest request) {
        Account account = accountRepo.findById(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal depositAmount = BigDecimal.valueOf(request.getAmount());
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        account.setBalance(account.getBalance().add(depositAmount));
        accountRepo.save(account);

        Transaction txn = new Transaction();
        txn.setAccountNumber(account.getAccountNumber());
        txn.setType("CREDIT");
        txn.setAmount(depositAmount);
        txn.setTimestamp(LocalDateTime.now());
        txn.setDescription("Deposit of ₹" + depositAmount);
        transactionRepo.save(txn);

        return "Deposit successful. New balance: ₹" + account.getBalance();
    }

    @Override
    public UserResponseDTO getUserByAccountNumber(String accountNumber) {
        User user = userRepo.findByAccount_AccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        UserResponseDTO dto = new UserResponseDTO();
        dto.setFullName(user.getFullName());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setAccountNumber(user.getAccount().getAccountNumber());
        dto.setBalance(user.getAccount().getBalance().doubleValue());
        return dto;
    }

    @Override
    public PublicUserResponseDTO getPublicUserView(String accountNumber) {
        User user = userRepo.findByAccount_AccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Account account = user.getAccount();

        PublicUserResponseDTO dto = new PublicUserResponseDTO();
        dto.setFullName(user.getFullName());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance().doubleValue());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setPanNumber(user.getPanNumber());
        dto.setAadharNumber(user.getAadharNumber());
        dto.setNationality(user.getNationality());
        return dto;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepo.findAll();
        List<UserResponseDTO> responseList = new ArrayList<>();

        for (User user : users) {
            Account account = user.getAccount();

            UserResponseDTO dto = new UserResponseDTO();
            dto.setFullName(user.getFullName());
            dto.setAccountNumber(account.getAccountNumber());
            dto.setBalance(account.getBalance().doubleValue());
            dto.setAge(user.getAge());
            dto.setGender(user.getGender());
            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public String debitAmount(DebitRequest request) {
        Account account = accountRepo.findById(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal debitAmount = BigDecimal.valueOf(request.getAmount());
        if (account.getBalance().compareTo(debitAmount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(debitAmount));
        accountRepo.save(account);

        Transaction txn = new Transaction();
        txn.setAccountNumber(account.getAccountNumber());
        txn.setType("DEBIT");
        txn.setAmount(debitAmount);
        txn.setTimestamp(LocalDateTime.now());
        txn.setDescription(request.getDescription());
        transactionRepo.save(txn);

        return "Amount debited successfully from account: " + request.getAccountNumber();
    }
}
