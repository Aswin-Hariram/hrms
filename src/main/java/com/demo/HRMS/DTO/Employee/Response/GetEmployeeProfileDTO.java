package com.demo.HRMS.DTO.Employee.Response;



import com.demo.HRMS.DTO.LeaveSheet.Response.LeaveSheetResponseDTO;
import com.demo.HRMS.EmployeeRole;

import com.demo.HRMS.Entities.LeaveSheetEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetEmployeeProfileDTO {

    private Long empID;
    private String empFirstName;
    private String empLastName;
    private String empEmail;
    private String empPhoneNumber;
    private LocalDate empDOB;
    private Integer age;
    private LocalDate empJoiningDate;
   private String empType;
    private String empStatus;
    private EmployeeRole empRole;
    private String ReportingToHR;
    private List<LeaveSheetResponseDTO> leaveSheetEntitiesList;
}
