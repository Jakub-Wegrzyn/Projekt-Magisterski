package com.projekt.magisterski.backend.energy.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergyUsageDto {
    private Long id;
    private String formattedDate;
    private Double aPlus;
    private Double aMinus;
}
