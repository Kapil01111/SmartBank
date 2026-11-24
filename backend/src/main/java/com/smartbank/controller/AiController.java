package com.smartbank.controller;
import com.smartbank.repository.CustomerRepository;
import com.smartbank.service.AiInsightService;
import com.smartbank.exception.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/ai")
public class AiController {
    private final AiInsightService ai; private final CustomerRepository customers;
    public AiController(AiInsightService ai,CustomerRepository customers) { this.ai=ai; this.customers=customers; }
    @PostMapping("/insights") public Map<String,Object> insights(Authentication auth) {
        Long id=customers.findByEmail(auth.getName()).orElseThrow(()->new NotFoundException("Customer not found")).getId();
        return ai.insightsForCustomer(id);
    }
}
