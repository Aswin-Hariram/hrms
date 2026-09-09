package com.demo.HRMS.DTO.Designation;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDesignationRequest {


    @NotBlank(message = "Designation name must not be empty")
    private String designationName;
    @NotNull(message = "Org ID is required")
    private Long orgID;
    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
