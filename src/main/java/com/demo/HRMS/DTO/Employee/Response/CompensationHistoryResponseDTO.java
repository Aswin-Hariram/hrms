package com.demo.HRMS.DTO.Employee.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompensationHistoryResponseDTO {

    private Long empID;
    private String employeeName;
    private int totalRevisions;
    private CompensationResponseDTO active;
    private List<CompensationResponseDTO> previousRevisions;
}
