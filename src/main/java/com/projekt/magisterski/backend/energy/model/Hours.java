package com.projekt.magisterski.backend.energy.model;

import lombok.Data;

@Data
public class Hours {

    private int hourStart;
    private int hourEnd;

    public Hours(int hourStart, int hourEnd) {
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;
    }
}
