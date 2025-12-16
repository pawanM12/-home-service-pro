package com.homeservice.customerservice.service;

import com.homeservice.customerservice.entity.Customer;
import com.homeservice.customerservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.homeservice.customerservice.client.ServiceProviderClient providerClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Try finding a Customer
        var customerOpt = repository.findByUsername(username);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return User.builder()
                    .username(customer.getUsername())
                    .password(customer.getPassword())
                    .roles("USER")
                    .build();
        }

        // 2. If not found, try finding a Provider (via Client)
        // We will assume that if standard customer login fails, we check for provider.
        // NOTE: Standard practice would separate these or use a common user table.
        // For this V2 retrofit, we do this.

        try {
            com.homeservice.customerservice.model.ServiceProvider provider = providerClient
                    .getProviderByEmail(username);
            if (provider != null) {
                // Return provider details.
                // NOTE: Password must be hashed in the DB same as customers (BCrypt).
                return User.builder()
                        .username(provider.getEmail())
                        .password(provider.getPassword())
                        .roles("PROVIDER")
                        .build();
            }
        } catch (Exception e) {
            // Log or ignore
        }

        throw new UsernameNotFoundException("User not found");
    }

    public void registerCustomer(String username, String password, String email) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setEmail(email);
        repository.save(customer);
    }

    public Customer getCustomerByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }
}
