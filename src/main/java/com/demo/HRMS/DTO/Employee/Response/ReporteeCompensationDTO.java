package com.demo.HRMS.DTO.Employee.Response;


import com.demo.HRMS.Types.PayType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteeCompensationDTO {

    private Long empID;
    private String employeeName;
    private String employeeEmail;
    private PayType payType;
    private BigDecimal hourlyRate;
    private BigDecimal stipend;
    private BigDecimal basicSalary;
    private BigDecimal pfPercentage;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate effectiveFrom;

    private boolean hasCompensation;
}
