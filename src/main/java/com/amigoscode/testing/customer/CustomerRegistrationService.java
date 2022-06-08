package com.amigoscode.testing.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public void registerNewCustomer(CustomerRegistrationRequest request) {

        String phoneNumber = request.getCustomer().getPhoneNumber();

        //TODO: Validate that phone number is valid

        Optional<Customer> customerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.getName().equals(request.getCustomer().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("phone number [%s] is taken", phoneNumber));
        }

        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }


        customerRepository.save(request.getCustomer());
    }
}
