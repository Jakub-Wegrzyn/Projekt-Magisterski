package com.projekt.magisterski.backend.energy.web.dto;

import com.projekt.magisterski.backend.energy.model.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoicesDto {
    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate billingPeriodFrom;
    private LocalDate billingPeriodUntil;
    private LocalDate issueLastDatePayment;;
    private Customer customer;
    private Double ammount;
    private String paymentStatus;
    private String pdfName;
    private byte[] pdfData;
}
