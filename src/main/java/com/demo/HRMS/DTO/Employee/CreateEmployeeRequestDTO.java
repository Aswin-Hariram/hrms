package com.demo.HRMS.DTO.Employee;

import com.demo.HRMS.Types.EmployeeRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequestDTO {

    @NotNull(message = "Organisation ID is required")
    private Long orgID;

    @NotBlank(message = "First name is required")
    private String empFirstName;

    @NotBlank(message = "Last name is required")
    private String empLastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String empEmail;

    @NotBlank(message = "Phone number is required")
    private String empPhoneNumber;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate empDOB;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 60, message = "Age must not exceed 60")
    private Integer age;

    @NotNull(message = "Joining date is required")
    private LocalDate empJoiningDate;

    @NotBlank(message = "Employee type is required")
    private String empType;

    @NotBlank(message = "Employee status is required")
    private String empStatus;

    private Long designationId;

    private Long departmentId;

    private Long reportToHr;

    @NotNull(message = "Employee role is required")
    private EmployeeRole empRole;
}