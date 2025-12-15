package com.projekt.magisterski.backend.energy.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergyUsageDtoWithPPE {
    private String id;
    private String PPE;
    private String formattedDate;
    private String aPlus;
    private String aMinus;
}
