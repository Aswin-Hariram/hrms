package com.demo.HRMS.DTO.EmployeesLeaveData;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLeaveSheetDTO {

    @NotNull(message = "Employee ID is required")
    private Long empID;


    @NotNull(message = "Organisation ID is required")
    private Long orgID;

    @NotEmpty(message = "Leave IDs are required")
    private List<Long> leaveIDs;

}