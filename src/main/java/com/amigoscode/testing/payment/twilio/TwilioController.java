package com.amigoscode.testing.payment.twilio;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/sms")
public class TwilioController {

    private final TwilioService twilioService;

    @PostMapping
    public void sendSms(@Valid @RequestBody SmsRequest smsRequest){
        twilioService.sendSms(smsRequest);
    }
}
