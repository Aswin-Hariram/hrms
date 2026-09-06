package com.demo.HRMS.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "organisation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgID;

    @NotBlank(message = "Organisation name is required")
    @Size(max = 100, message = "Organisation name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String orgName;

    @NotBlank(message = "Organisation email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String orgEmail;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone must contain exactly 10 digits"
    )
    @Column(nullable = false, length = 10)
    private String orgPhone;

    @NotBlank(message = "Address is required")
    private String orgAddress;

    @NotBlank(message = "City is required")
    private String orgCity;

    @NotBlank(message = "State is required")
    private String orgState;

    @NotBlank(message = "Country is required")
    private String orgCountry;


    @Column(nullable = false)
    private String orgStatus="Active";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}