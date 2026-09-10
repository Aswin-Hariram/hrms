package com.demo.HRMS.DTO.Employee.Response;

import com.demo.HRMS.Types.PayType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompensationResponseDTO {

    private Long compensationId;
    private Long empID;
    private String employeeName;
    private PayType payType;

    private BigDecimal hourlyRate;
    private BigDecimal stipend;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal pfPercentage;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate effectiveFrom;

    private boolean active;
    private String revisionReason;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
}