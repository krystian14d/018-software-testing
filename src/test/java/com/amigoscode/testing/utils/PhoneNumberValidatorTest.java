package com.amigoscode.testing.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }


    @ParameterizedTest
    @CsvSource({
            "+447000000000,true",
            "+4470000000456487,false",
            "4470000000456487,false"
    })
    void itShouldValidatePhoneNumber(String phoneNumber, String expected) {
        //given

        //when
        boolean isValid = underTest.test(phoneNumber);

        //then
        assertThat(isValid).isEqualTo(expected);
    }

//    @Test
//    @DisplayName("Should fail when length is bigger than 13")
//    void itShouldValidatePhoneNumberWhenIncorrectAndHasLengthBiggerThan13() {
//
//        //given
//        String phoneNumber = "+4470000000456487";
//
//        //when
//        boolean isValid = underTest.test(phoneNumber);
//
//        //then
//        assertThat(isValid).isFalse();
//    }
//
//    @Test
//    @DisplayName("Should fail when does not start with +")
//    void itShouldValidatePhoneNumberWhenDoesNotStartWithPlusSign() {
//
//        //given
//        String phoneNumber = "4470000000456487";
//
//        //when
//        boolean isValid = underTest.test(phoneNumber);
//
//        //then
//        assertThat(isValid).isFalse();
//    }
}
