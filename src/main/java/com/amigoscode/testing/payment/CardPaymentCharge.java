package com.amigoscode.testing.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CardPaymentCharge {

    private final boolean isCardDebited;
}
