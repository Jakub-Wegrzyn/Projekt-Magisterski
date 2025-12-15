package com.projekt.magisterski.backend.energy.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagramsDto {
    private long energyUsageMidnightToSix;
    private long energyUsageSixToTwelve;
    private long energyUsageTwelveToEighteen;
    private long energyUsageEighteenToMidnight;
}
