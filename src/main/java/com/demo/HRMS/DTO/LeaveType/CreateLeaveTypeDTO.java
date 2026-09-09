package com.demo.HRMS.DTO.LeaveType;

import com.demo.HRMS.LeaveTypesCodes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeaveTypeDTO {

    @NotNull(message = "Organisation ID is required.")
    private Long orgID;

    @NotBlank(message = "Leave name should be mentioned.")
    private String leaveName;

    @NotNull(message = "Leave code is required.")
    private String leaveCode;

    @NotNull(message = "Paid status is required.")
    private Boolean ispaid;
    @NotNull(message = "Paid status is required.")
    private Boolean isActive;

    @NotNull
    private int noDays;
    @NotNull(message = "Approval requirement is required.")
    private Boolean approvalRequired;
}