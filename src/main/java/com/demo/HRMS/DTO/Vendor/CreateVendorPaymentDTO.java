package com.demo.HRMS.DTO.Vendor;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVendorPaymentDTO {

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Payment date is required")
    @PastOrPresent(message = "Payment date cannot be in the future")
    private LocalDate paymentDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 13, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @NotBlank(message = "Payment mode is required")
    @Size(max = 20, message = "Payment mode cannot exceed 20 characters")
    private String paymentMode;

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    private String referenceNumber;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
}