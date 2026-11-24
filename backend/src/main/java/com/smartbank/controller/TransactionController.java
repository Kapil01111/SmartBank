package com.smartbank.controller;
import com.smartbank.dto.ApiDtos.*;
import com.smartbank.service.BankingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api")
public class TransactionController {
    private final BankingService banking;
    public TransactionController(BankingService banking) { this.banking=banking; }
    @PostMapping("/transfers") public List<TransactionResponse> transfer(Authentication a,@Valid @RequestBody TransferRequest r) { return banking.transfer(a.getName(),r); }
    @GetMapping("/transactions") public List<TransactionResponse> history(Authentication a) { return banking.history(a.getName()); }
}
