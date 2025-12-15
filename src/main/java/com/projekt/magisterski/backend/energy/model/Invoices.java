package com.projekt.magisterski.backend.energy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Numer_faktury", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(name = "data_wystawienia", nullable = false)
    private LocalDate issueDate;

    @Column(name = "okres_rozliczeniowy_od", nullable = false)
    private LocalDate billingPeriodFrom;

    @Column(name = "okres_rozliczeniowy_do", nullable = false)
    private LocalDate billingPeriodUntil;

    @Column(name = "termin_platnosi", nullable = false)
    private LocalDate issueLastDatePayment;;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Customer customer;

    @Column(name = "kwota", nullable = false)
    private Double ammount;

    @Column(name = "staus płatności")
    private String paymentStatus;

    @Column(name = "nazwa_PDF")
    private String pdfName;

    @Lob
    @Column(name = "plik_pdf", columnDefinition = "LONGBLOB")
    private byte[] pdfData;

}
