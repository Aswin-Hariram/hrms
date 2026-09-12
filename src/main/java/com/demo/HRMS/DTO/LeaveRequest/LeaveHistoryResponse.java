package com.demo.HRMS.DTO.LeaveRequest;

import com.demo.HRMS.Types.LeaveRequestStatus;

import java.time.LocalDate;

public record LeaveHistoryResponse(
        Long leaveId,
        String leaveName,
        Long requestId,
        String leaveCode,
        LocalDate startDate,
        LocalDate endDate,
        int noOfDays,
        boolean isPaid,
        LeaveRequestStatus status
) {}
