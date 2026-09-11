package com.demo.HRMS.DTO.Vendor;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPaymentResponse {

    private Long id;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String paymentMode;
    private String referenceNumber;
    private String remarks;


    private Long invoiceId;
    private String invoiceNumber;
    private BigDecimal invoiceTotalAmount;
    private BigDecimal invoicePaidAmount;
    private BigDecimal invoiceBalanceAmount;
    private String invoiceStatus;


    private Long createdBy;
    private LocalDateTime createdAt;
}