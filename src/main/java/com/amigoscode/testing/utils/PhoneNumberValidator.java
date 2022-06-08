package com.amigoscode.testing.utils;

import java.util.function.Predicate;

public class PhoneNumberValidator implements Predicate<String> {

    @Override
    public boolean test(String phoneNumber) {
        if(phoneNumber.startsWith("+44") && phoneNumber.length() == 13){
            return true;
        }
        return false;
    }
}
