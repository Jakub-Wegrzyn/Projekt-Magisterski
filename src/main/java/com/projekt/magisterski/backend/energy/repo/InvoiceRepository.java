package com.projekt.magisterski.backend.energy.repo;

import com.projekt.magisterski.backend.energy.model.Invoices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoices, Long> {
    @Modifying
    @Query("UPDATE Invoices i SET i.paymentStatus = :paymentStatus WHERE i.invoiceNumber = :invoiceNumber")
    void updatePaymentStatus(@Param("invoiceNumber") String invoiceNumber, @Param("paymentStatus") String paymentStatus);
}
