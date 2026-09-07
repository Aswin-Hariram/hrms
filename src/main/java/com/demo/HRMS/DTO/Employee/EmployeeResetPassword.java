package com.demo.HRMS.DTO.Employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeResetPassword {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String empEmail;

    @NotBlank(message = "Current password cannot be empty")
    private String empPassword;

    @NotNull(message = "Organisation ID cannot be empty")
    private Long orgID;

    @NotBlank(message = "New password cannot be empty")
    @Size(min = 5, message = "Password must be at least 5 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{5,}$",
            message = "Password must contain at least one uppercase, one lowercase, and one special character"
    )
    private String empNewPassword;
}