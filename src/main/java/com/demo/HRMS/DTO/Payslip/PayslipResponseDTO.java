package com.demo.HRMS.DTO.Payslip;

import com.demo.HRMS.Types.PayslipStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipResponseDTO {

    private Long payslipId;

    private Long empId;
    private String empName;
    private String empEmail;
    private String department;
    private String designation;

    private int payMonth;
    private int payYear;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    private Long compensationId;
    private LocalDate compensationEffectiveFrom;

    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal stipend;


    private BigDecimal overtimePay;
    private BigDecimal bonus;
    private BigDecimal grossEarnings;


    private BigDecimal pfPercentage;
    private BigDecimal pfAmount;          // computed: basic * pf% / 100
    private BigDecimal taxAmount;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductions;


    private BigDecimal netPay;


    private int totalWorkingDays;
    private int daysPresent;
    private int leavesTaken;
    private int unpaidLeaves;

    private PayslipStatus status;
    private LocalDate generatedDate;
    private LocalDate paidDate;
    private String remarks;
    private LocalDateTime createdAt;
}
