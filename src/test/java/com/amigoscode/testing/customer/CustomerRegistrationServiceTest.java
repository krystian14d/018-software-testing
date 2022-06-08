package com.amigoscode.testing.customer;

import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    //alternative method to init mock:
//    private CustomerRepository customerRepository = mock(CustomerRepository.class);

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new CustomerRegistrationService(phoneNumberValidator, customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //given a phone number and customer
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);

        //...and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //no customer with phone number found
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).usingRecursiveComparison().isEqualTo(customer);
    }

    @Test
    void itShouldNotSaveNewCustomerWhenPhoneNumberIsInvalid() {
        //given a phone number and customer
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Mariam", phoneNumber);

        //...and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

        //when
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Phone Number " + phoneNumber + " is not valid.");

        //then
        then(customerRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //given a phone number and customer
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "Mariam", phoneNumber);

        //...and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //no customer with phone number found
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).usingRecursiveComparison().ignoringFields("id").isEqualTo(customer);
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {

        //given a phone number and customer
        String phoneNumber = "000099";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Mariam", phoneNumber);

        //...and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //..no customer with phone number found
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        //valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);


        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {

        //given a phone number and customer
        String phoneNumber = "000099";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Mariam", phoneNumber);
        Customer customer2 = new Customer(UUID.randomUUID(), "Anna", phoneNumber);

        //...and a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //exists another customer with the same phone number
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer2));

        //valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //when    //then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));

        //finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }


}