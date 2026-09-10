package com.demo.HRMS.DTO.LeaveRequest.Response;

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
public class PendingLeaveResponse {

    private Long requestID;


    private Long employeeID;
    private String employeeName;
    private String employeeEmail;


    private Long leaveID;
    private String leaveName;
    private int noOfDays;
    private LocalDate startDate;
    private LocalDate endDate;

    private LeaveRequestStatus status;
    private String reason;

    private Long requestedToID;
    private String requestedToName;

    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;
}
