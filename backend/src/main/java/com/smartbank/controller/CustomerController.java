package com.smartbank.controller;
import com.smartbank.dto.ApiDtos.CustomerResponse;
import com.smartbank.repository.CustomerRepository;
import com.smartbank.exception.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository customers;
    public CustomerController(CustomerRepository customers) { this.customers=customers; }
    @GetMapping("/me") public CustomerResponse me(Authentication auth) {
        var c=customers.findByEmail(auth.getName()).orElseThrow(()->new NotFoundException("Customer not found"));
        return new CustomerResponse(c.getId(),c.getFullName(),c.getEmail(),c.getCreatedAt());
    }
}
