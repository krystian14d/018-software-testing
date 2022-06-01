package com.amigoscode.testing.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        //1. PhoneNumber is taken
        //2. if taken lets check if belongs to same customer
        // - 2.1 if yes return
        // - 2.2 throw an exception
        //3. Save customer
    }
}
