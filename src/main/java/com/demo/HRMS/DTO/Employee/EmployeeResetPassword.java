package com.demo.HRMS.DTO.Employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class EmployeeResetPassword {


    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    String empEmail;

    @NotBlank(message = "Password cannot be empty")
    String empPassword;

    @NotBlank(message = "New Password cannot be empty")
    @Size(min = 5, message = "Password must be at least 5 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{5,}$",
            message = "Password must contain at least one uppercase, one lowercase, and one special character"
    )
    String empNewPassword;
}
