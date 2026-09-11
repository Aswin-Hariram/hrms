package com.demo.HRMS.DTO.Vendor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateVendorDTO {

    @NotBlank(message = "Vendor name is required")
    @Size(min = 2, max = 100, message = "Vendor name must be between 2 and 100 characters")
    private String vendorName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    private String address;

    private String contactPerson;

    @Pattern(regexp = "^[0-9A-Z]{15}$", message = "GST number must be 15 alphanumeric characters")
    private String gstNumber;
}