package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;


class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        //given
        UUID customerId = UUID.randomUUID();

        //...customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //...payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );

        //... card is charged succesfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        //when
        underTest.chargeCard(customerId, paymentRequest);

        //then
        ArgumentCaptor<Payment> paymentArgumentCaptor =
                ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

        assertThat(paymentArgumentCaptorValue).usingRecursiveComparison().ignoringFields("customerId")
                .isEqualTo(paymentRequest.getPayment());

        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        //given
        UUID customerId = UUID.randomUUID();

        //...customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //...payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );

        //... card is not charged succesfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        //when //then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for customer %s", customerId));

        //... no interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        //given
        UUID customerId = UUID.randomUUID();

        //...customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //euros
        Currency currency = Currency.EUR;

        //...payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        currency,
                        "card123xx",
                        "Donation"
                )
        );

        //when
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Currency [" + currency + "] not supported");

        //then

        //... no interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();

        //... no interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCustomerNotFound() {
        //given
        UUID customerId = UUID.randomUUID();
        //...customer not found exist in db
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        //...payment request
        PaymentRequest paymentRequest = new PaymentRequest(new Payment());
        //when
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("customer with id[%s] not found", customerId));

        //then

        //... no interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();

        //... no interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();

    }
}