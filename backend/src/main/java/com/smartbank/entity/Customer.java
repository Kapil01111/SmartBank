package com.smartbank.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100) private String fullName;
    @Column(nullable = false, unique = true, length = 150) private String email;
    @Column(nullable = false) private String password;
    @Column(nullable = false, length = 20) private String role = "CUSTOMER";
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    protected Customer() {}
    public Customer(String fullName, String email, String password) {
        this.fullName = fullName; this.email = email; this.password = password;
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
