package com.demo.HRMS.DTO.Payslip;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BulkPayslipRequest {

    private Long orgId;


    @NotNull
    private Long departmentId;


    private LocalDate periodStart;
    
    private LocalDate periodEnd;
}