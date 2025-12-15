package com.projekt.magisterski.backend.energy.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SumEnergyUsageDto {
    Double suma_a_plus;
    Double suma_a_minus;
}
