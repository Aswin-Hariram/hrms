package com.demo.HRMS.DTO.Department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateDepartmentRequest {


    @NotBlank(message = "Department Name must not be empty.")
    String departmentName;

    @NotNull(message = "Organisation ID must not be null.")
    Long orgID;


}
