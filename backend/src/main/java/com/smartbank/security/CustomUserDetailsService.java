package com.smartbank.security;
import com.smartbank.repository.CustomerRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final CustomerRepository customers;
    public CustomUserDetailsService(CustomerRepository customers) { this.customers = customers; }
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var c = customers.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
        return User.withUsername(c.getEmail()).password(c.getPassword()).roles(c.getRole()).build();
    }
}
