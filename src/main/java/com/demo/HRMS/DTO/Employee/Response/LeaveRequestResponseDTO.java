package com.demo.HRMS.DTO.Employee.Response;

import com.demo.HRMS.LeaveRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestResponseDTO {

    private Long requestID;

    private Long empID;
    private String employeeName;

    private Long orgID;

    private Long leaveID;
    private String leaveName;

    private int noOfDays;

    private LocalDate startDate;
    private LocalDate endDate;

    private LeaveRequestStatus status;

    private String reason;

    private Long requestedToEmpID;
    private String requestedToName;

    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;
}
