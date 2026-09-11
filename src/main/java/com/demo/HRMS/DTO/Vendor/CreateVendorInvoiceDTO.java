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
public class CreateVendorInvoiceDTO {

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    private Long contractId;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 100, message = "Invoice number cannot exceed 100 characters")
    private String invoiceNumber;

    @NotNull(message = "Invoice date is required")
    @PastOrPresent(message = "Invoice date cannot be in the future")
    private LocalDate invoiceDate;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or later")
    private LocalDate dueDate;

    @NotNull(message = "Sub total is required")
    @DecimalMin(value = "0.00", message = "Sub total cannot be negative")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal subTotal;

    @DecimalMin(value = "0.00", message = "Tax amount cannot be negative")
    @Digits(integer = 13, fraction = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    @Digits(integer = 13, fraction = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Size(max = 3, message = "Currency must be a 3-letter code")
    @Builder.Default
    private String currency = "INR";

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}