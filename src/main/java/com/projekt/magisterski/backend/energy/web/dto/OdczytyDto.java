package com.projekt.magisterski.backend.energy.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OdczytyDto {
    private Long singleMeasure;
    private Long customerId;
}
