package com.amigoscode.testing.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Customer {

    private UUID id;

    @NotBlank
    private String name;

    private String phoneNumber;
}
