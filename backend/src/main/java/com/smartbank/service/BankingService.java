package com.smartbank.service;

import com.smartbank.dto.ApiDtos.*;
import com.smartbank.entity.*;
import com.smartbank.exception.*;
import com.smartbank.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

@Service
public class BankingService {
    private final CustomerRepository customers; private final BankAccountRepository accounts; private final BankTransactionRepository transactions;
    private final SecureRandom random = new SecureRandom();
    public BankingService(CustomerRepository customers, BankAccountRepository accounts, BankTransactionRepository transactions) {
        this.customers=customers; this.accounts=accounts; this.transactions=transactions;
    }
    @Transactional public AccountResponse createAccount(String email, AccountRequest req) {
        Customer customer=customer(email);
        String number="SB"+String.format("%012d", Math.abs(random.nextLong()%1_000_000_000_000L));
        BankAccount a=accounts.save(new BankAccount(number,req.accountType(),customer));
        return accountDto(a);
    }
    @Transactional(readOnly=true) public List<AccountResponse> listAccounts(String email) {
        Customer c=customer(email); return accounts.findAllByCustomerIdOrderByCreatedAtDesc(c.getId()).stream().map(this::accountDto).toList();
    }
    @Transactional(readOnly=true) public AccountResponse getAccount(String email, Long id) {
        return accountDto(ownedAccount(email,id));
    }
    @Transactional public TransactionResponse deposit(String email, Long id, MoneyRequest req) {
        BankAccount a=lockOwned(email,id); BigDecimal amount=money(req.amount());
        a.setBalance(a.getBalance().add(amount));
        return txDto(transactions.save(new BankTransaction("DEPOSIT",amount,req.description(),a)));
    }
    @Transactional public TransactionResponse withdraw(String email, Long id, MoneyRequest req) {
        BankAccount a=lockOwned(email,id); BigDecimal amount=money(req.amount());
        if (a.getBalance().compareTo(amount)<0) throw new BadRequestException("Insufficient balance");
        a.setBalance(a.getBalance().subtract(amount));
        return txDto(transactions.save(new BankTransaction("WITHDRAWAL",amount,req.description(),a)));
    }
    @Transactional public List<TransactionResponse> transfer(String email, TransferRequest req) {
        if (req.fromAccountId().equals(req.toAccountId())) throw new BadRequestException("Source and destination accounts must differ");
        BigDecimal amount=money(req.amount());
        // Lock accounts in ID order to reduce deadlock risk.
        Long first=Math.min(req.fromAccountId(),req.toAccountId()), second=Math.max(req.fromAccountId(),req.toAccountId());
        BankAccount one=lockOwned(email,first), two=lockOwned(email,second);
        BankAccount from=req.fromAccountId().equals(first)?one:two;
        BankAccount to=req.toAccountId().equals(first)?one:two;
        if (from.getBalance().compareTo(amount)<0) throw new BadRequestException("Insufficient balance");
        from.setBalance(from.getBalance().subtract(amount)); to.setBalance(to.getBalance().add(amount));
        String desc=req.description()==null?"Transfer":req.description();
        var debit=transactions.save(new BankTransaction("TRANSFER_OUT",amount,desc+" (to "+to.getAccountNumber()+")",from));
        var credit=transactions.save(new BankTransaction("TRANSFER_IN",amount,desc+" (from "+from.getAccountNumber()+")",to));
        return List.of(txDto(debit),txDto(credit));
    }
    @Transactional(readOnly=true) public List<TransactionResponse> history(String email) {
        Customer c=customer(email); return transactions.findAllByAccountCustomerIdOrderByCreatedAtDesc(c.getId()).stream().map(this::txDto).toList();
    }
    private Customer customer(String email) { return customers.findByEmail(email).orElseThrow(()->new NotFoundException("Customer not found")); }
    private BankAccount ownedAccount(String email,Long id) {
        Customer c=customer(email); return accounts.findByIdAndCustomerId(id,c.getId()).orElseThrow(()->new NotFoundException("Account not found"));
    }
    private BankAccount lockOwned(String email,Long id) {
        BankAccount a=accounts.findByIdForUpdate(id).orElseThrow(()->new NotFoundException("Account not found"));
        if (!a.getCustomer().getEmail().equalsIgnoreCase(email)) throw new NotFoundException("Account not found");
        if (!"ACTIVE".equals(a.getStatus())) throw new BadRequestException("Account is not active");
        return a;
    }
    private BigDecimal money(BigDecimal n) {
        if(n==null || n.scale()>2 || n.compareTo(BigDecimal.ZERO)<=0) throw new BadRequestException("Amount must be positive and have at most 2 decimal places");
        return n;
    }
    private AccountResponse accountDto(BankAccount a) { return new AccountResponse(a.getId(),a.getAccountNumber(),a.getAccountType(),a.getBalance(),a.getStatus(),a.getCreatedAt()); }
    private TransactionResponse txDto(BankTransaction t) { return new TransactionResponse(t.getId(),t.getType(),t.getAmount(),t.getStatus(),t.getDescription(),t.getAccount().getId(),t.getCreatedAt()); }
}
