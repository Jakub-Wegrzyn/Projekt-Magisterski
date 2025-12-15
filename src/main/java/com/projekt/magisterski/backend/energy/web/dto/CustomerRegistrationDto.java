package com.projekt.magisterski.backend.energy.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegistrationDto {
    private String firstName;
    private String lastName;
    private String PPE;
    private String contractNumber;
    private String email;
    private LocalDate birthDate;
    private boolean prosument;
}
