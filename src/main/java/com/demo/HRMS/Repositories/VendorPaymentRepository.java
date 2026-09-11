package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.VendorPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VendorPaymentRepository extends JpaRepository<VendorPayment, Long> {

    List<VendorPayment> findByInvoiceIdOrderByPaymentDateDesc(Long invoiceId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM VendorPayment p WHERE p.invoice.id = :invoiceId")
    BigDecimal sumAmountByInvoiceId(@Param("invoiceId") Long invoiceId);
}