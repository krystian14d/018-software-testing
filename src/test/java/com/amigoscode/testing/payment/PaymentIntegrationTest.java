package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationController;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    private CustomerRegistrationController customerRegistrationController;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {

        //given a customer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "James", "+447000000000");

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        //register customer
        ResultActions customerRegResultActions = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))));

        //Payment
        long paymentId = 1L;
        Payment payment = new Payment(paymentId, customerId, new BigDecimal("100.00"), Currency.GBP, "x0x0x0x0", "Donation");

        //Payment request
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        //when payment is sent
        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        //then both customer registration and payment requests are 200 status code
        customerRegResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());

        //payment is stored in DB
        //TODO: Do not use payment repository, instead create an endpoint to retrieve payments for customers
        assertThat(paymentRepository.findById(paymentId)).isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p)
                            .usingRecursiveComparison()
                            .isEqualTo(payment);
                });

        //TODO: Ensure SMS is delivered

    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to Json");
            return null;
        }
    }
}
