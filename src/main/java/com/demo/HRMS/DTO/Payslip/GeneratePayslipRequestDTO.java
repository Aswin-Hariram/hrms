package com.demo.HRMS.DTO.Payslip;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratePayslipRequestDTO {

    @NotNull
    private Long empId;

    @NotNull @Min(1) @Max(12)
    private int payMonth;

    @NotNull @Min(2000)
    private int payYear;

    @Builder.Default
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal bonus = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    private int totalWorkingDays;
    private int daysPresent;
    private int leavesTaken;
    private int unpaidLeaves;

    private String remarks;
}