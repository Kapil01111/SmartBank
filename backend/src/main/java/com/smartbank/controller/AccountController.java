package com.smartbank.controller;
import com.smartbank.dto.ApiDtos.*;
import com.smartbank.service.BankingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/accounts")
public class AccountController {
    private final BankingService banking;
    public AccountController(BankingService banking) { this.banking=banking; }
    @PostMapping public AccountResponse create(Authentication a,@Valid @RequestBody AccountRequest r) { return banking.createAccount(a.getName(),r); }
    @GetMapping public List<AccountResponse> list(Authentication a) { return banking.listAccounts(a.getName()); }
    @GetMapping("/{id}") public AccountResponse get(Authentication a,@PathVariable Long id) { return banking.getAccount(a.getName(),id); }
    @PostMapping("/{id}/deposit") public TransactionResponse deposit(Authentication a,@PathVariable Long id,@Valid @RequestBody MoneyRequest r) { return banking.deposit(a.getName(),id,r); }
    @PostMapping("/{id}/withdraw") public TransactionResponse withdraw(Authentication a,@PathVariable Long id,@Valid @RequestBody MoneyRequest r) { return banking.withdraw(a.getName(),id,r); }
}
