package com.demo.HRMS.DTO.Payslip;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkPayslipResponse {
    private int totalRequested;
    private int successCount;
    private int skippedCount;
    private int failedCount;
    private List<PayslipResponse> payslips;
    private List<String> errors;
}
