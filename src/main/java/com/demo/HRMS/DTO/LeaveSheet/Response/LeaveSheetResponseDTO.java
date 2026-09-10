package com.demo.HRMS.DTO.LeaveSheet.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveSheetResponseDTO {




    private Long leaveID;

    private String leaveName;

    private int allocatedDays;
    private int usedDays;
    private int remainingDays;
}