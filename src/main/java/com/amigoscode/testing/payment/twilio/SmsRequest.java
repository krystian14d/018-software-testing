package com.amigoscode.testing.payment.twilio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@AllArgsConstructor
public class SmsRequest {

    private final String phoneNumber; //destination
    private final String message;

}
