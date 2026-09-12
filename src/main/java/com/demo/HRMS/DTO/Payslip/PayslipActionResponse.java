package com.demo.HRMS.DTO.Payslip;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PayslipActionResponse {
    private int requested;
    private int succeeded;
    private int skipped;
    private int failed;
    private List<Long> succeededIds;
    private List<String> errors;
}