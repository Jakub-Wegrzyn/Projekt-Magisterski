package com.projekt.magisterski.backend.energy.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class SelectedInvicesDTO {
    private List<String> selectedInvoiceNumbers;
}
