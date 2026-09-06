package com.demo.HRMS.DTO.Employee;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeLoginRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String empEmail;

    @NotBlank(message = "Password cannot be empty")
    private String empPassword;
}