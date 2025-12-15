package com.projekt.magisterski.backend.energy.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEnergyUsageForChart {
    private String sum_a_plus;
    private String sum_a_minus;
}
