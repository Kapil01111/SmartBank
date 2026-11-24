package com.smartbank.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 24) private String accountNumber;
    @Column(nullable = false, length = 20) private String accountType;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal balance = BigDecimal.ZERO;
    @Column(nullable = false, length = 20) private String status = "ACTIVE";
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Customer customer;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    protected BankAccount() {}
    public BankAccount(String accountNumber, String accountType, Customer customer) {
        this.accountNumber = accountNumber; this.accountType = accountType; this.customer = customer;
    }
    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public Customer getCustomer() { return customer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
