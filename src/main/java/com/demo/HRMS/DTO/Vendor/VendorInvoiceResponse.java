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
public class VendorInvoiceResponse {

    private Long id;
    private Long orgId;
    private Long vendorId;
    private String vendorName;


    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;

    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;

    private String currency;
    private String description;
    private String status;

    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}