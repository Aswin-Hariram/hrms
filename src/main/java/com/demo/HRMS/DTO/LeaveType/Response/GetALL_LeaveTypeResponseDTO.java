package com.demo.HRMS.DTO.LeaveType.Response;

import com.demo.HRMS.Entities.LeaveTypeEntity;
import com.demo.HRMS.LeaveTypesCodes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetALL_LeaveTypeResponseDTO {

    private Long leave_ID;

    private Long orgID;

    private String leave_Name;

    private LeaveTypesCodes leaveCode;

    private boolean active;

    private boolean approvalRequired;

    private boolean paid;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int noDays;

    public GetALL_LeaveTypeResponseDTO(LeaveTypeEntity savedLeaveEntity) {
        this.leave_ID = savedLeaveEntity.getLeaveId();
        this.orgID = savedLeaveEntity.getOrganisation().getOrgID();
        this.leave_Name = savedLeaveEntity.getLeave_Name();
        this.leaveCode = savedLeaveEntity.getLeaveCode();
        this.active = savedLeaveEntity.isActive();
        this.approvalRequired = savedLeaveEntity.isApprovalRequired();
        this.paid = savedLeaveEntity.isPaid();
        this.createdAt = savedLeaveEntity.getCreatedAt();
        this.updatedAt = savedLeaveEntity.getUpdatedAt();
        this.noDays = savedLeaveEntity.getNoDays();
    }
}

