package com.smartbank.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transactions")
public class BankTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 30) private String type;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 20) private String status = "SUCCESS";
    @Column(length = 255) private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private BankAccount account;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    protected BankTransaction() {}
    public BankTransaction(String type, BigDecimal amount, String description, BankAccount account) {
        this.type = type; this.amount = amount; this.description = description; this.account = account;
    }
    public Long getId() { return id; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public BankAccount getAccount() { return account; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
