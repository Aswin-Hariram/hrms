package com.demo.HRMS.DTO.Payslip;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PayRequest {

    @NotEmpty
    private List<Long> payslipIds;

    @NotBlank
    private String paymentMode;

    @NotBlank
    private String paymentReference;

    @NotNull
    private LocalDate paidOn;
}