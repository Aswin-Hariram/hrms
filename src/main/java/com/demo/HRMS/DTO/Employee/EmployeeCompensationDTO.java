package com.demo.HRMS.DTO.Employee;



import com.demo.HRMS.Types.PayType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeCompensationDTO {

    @NotNull
    private PayType payType;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal hourlyRate;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal stipend;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal pfPercentage;

    @NotNull
    private LocalDate effectiveFrom;
}