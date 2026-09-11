package com.demo.HRMS.DTO.Payslip;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkGeneratePayslipRequestDTO {

    @NotNull
    private Long orgId;

    @NotNull @Min(1) @Max(12)
    private int payMonth;

    @NotNull @Min(2000)
    private int payYear;

    private Long departmentId;
}
