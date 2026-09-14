package com.demo.HRMS.DTO.LeaveRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectRequestDTO {

    @NotNull(message = "request id is required")
    private Long reqId;

    private String reason;
}
