package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class PaymentIntegrationTest {


    private CustomerRegistrationController customerRegistrationController;

    @Test
    void itShouldCreatePaymentSuccessfully() {
        //given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "James", "123456");
        //when

        //then

    }
}
