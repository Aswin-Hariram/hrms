package com.demo.HRMS.DTO.Payslip;

import com.demo.HRMS.Types.EmploymentType;
import com.demo.HRMS.Types.PayType;
import com.demo.HRMS.Types.PayslipStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PayslipResponse {
    private Long payslipId;
    private Long orgId;
    private Long empID;
    private String employeeName;
    private String employeeEmail;
    private String departmentName;
    private String organisationName;
    private Integer unPaidLeaveTaken;

    private EmploymentType employmentType;
    private PayType payType;
    private PayslipStatus status;

    private LocalDate periodStart;
    private LocalDate periodEnd;
    private int totalDays;
    private int paidDays;

    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal stipend;
    private BigDecimal hourlyRate;
    private BigDecimal hourlyHours;
    private BigDecimal grossPay;

    private BigDecimal pfPercentage;
    private BigDecimal pfAmount;
    private BigDecimal netPay;

    private LocalDateTime generatedAt;
}