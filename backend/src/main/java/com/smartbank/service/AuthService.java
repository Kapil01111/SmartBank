package com.smartbank.service;
import com.smartbank.dto.ApiDtos.*;
import com.smartbank.entity.Customer;
import com.smartbank.exception.BadRequestException;
import com.smartbank.repository.CustomerRepository;
import com.smartbank.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final CustomerRepository customers; private final PasswordEncoder encoder;
    private final AuthenticationManager authManager; private final JwtService jwt;
    public AuthService(CustomerRepository customers, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwt) {
        this.customers=customers; this.encoder=encoder; this.authManager=authManager; this.jwt=jwt;
    }
    @Transactional public AuthResponse register(RegisterRequest request) {
        String email=request.email().trim().toLowerCase();
        if (customers.existsByEmail(email)) throw new BadRequestException("Email is already registered");
        Customer c=customers.save(new Customer(request.fullName().trim(), email, encoder.encode(request.password())));
        return response(c);
    }
    public AuthResponse login(LoginRequest request) {
        String email=request.email().trim().toLowerCase();
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        return response(customers.findByEmail(email).orElseThrow());
    }
    private AuthResponse response(Customer c) { return new AuthResponse(jwt.generate(c.getEmail(),c.getRole()),"Bearer",c.getId(),c.getFullName(),c.getEmail()); }
}
