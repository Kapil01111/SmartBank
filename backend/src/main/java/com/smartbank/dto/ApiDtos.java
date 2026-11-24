package com.smartbank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ApiDtos {
    public record RegisterRequest(@NotBlank @Size(max=100) String fullName,
        @NotBlank @Email @Size(max=150) String email,
        @NotBlank @Size(min=8, max=72) String password) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record AuthResponse(String token, String tokenType, Long customerId, String fullName, String email) {}
    public record AccountRequest(@NotBlank @Pattern(regexp="SAVINGS|CURRENT") String accountType) {}
    public record MoneyRequest(@NotNull @DecimalMin(value="0.01") @Digits(integer=17,fraction=2) BigDecimal amount,
        @Size(max=255) String description) {}
    public record TransferRequest(@NotNull Long fromAccountId, @NotNull Long toAccountId,
        @NotNull @DecimalMin(value="0.01") @Digits(integer=17,fraction=2) BigDecimal amount,
        @Size(max=255) String description) {}
    public record CustomerResponse(Long id, String fullName, String email, LocalDateTime createdAt) {}
    public record AccountResponse(Long id, String accountNumber, String accountType, BigDecimal balance,
        String status, LocalDateTime createdAt) {}
    public record TransactionResponse(Long id, String type, BigDecimal amount, String status,
        String description, Long accountId, LocalDateTime createdAt) {}
    public record MessageResponse(String message) {}
    private ApiDtos() {}
}
