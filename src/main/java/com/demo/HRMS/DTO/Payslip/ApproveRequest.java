package com.demo.HRMS.DTO.Payslip;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ApproveRequest {


    private List<Long> payslipIds;
    private Long departmentId;
    private java.time.LocalDate periodFrom;
    private java.time.LocalDate periodTo;
}