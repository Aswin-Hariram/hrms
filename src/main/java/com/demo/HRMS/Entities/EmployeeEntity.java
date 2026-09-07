package com.demo.HRMS.Entities;


import com.demo.HRMS.EmployeeRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Employee")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empID;

    @ManyToOne
    @JoinColumn(name = "orgID", nullable = false)
    private OrganisationEntity organisation;

    @Column(nullable = false)
    private String empFirstName;

    @Column(nullable = false)
    private String empLastName;


    @Column(nullable = false, unique = true)
    private String empEmail;

    @Column(nullable = false)
    private String empPhoneNumber;

    @Column(nullable = false)
    private String empPassword;

    @Column(nullable = false)
    @Past(message = "Date of Birth must be a date from past")
    private LocalDate empDOB;


    @Column(nullable = false)
    @Max(60)
    @Min(18)
    private int age;


    @Column(nullable = false)
    private LocalDate empJoiningDate;

    @Column(nullable = false)
    private String empType;

    @Column(nullable = false)
    private String empStatus;


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "designationId", nullable = true)
    private DesignationEntity designation;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "departmentId", nullable = true)
    private DepartmentEntity department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole empRole;

    @Column(nullable = false)
    private boolean defaultPasswordUpdated = false;


    @CreationTimestamp
    @Column(name = "Created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "Updated_at", nullable = false)
    private LocalDateTime updatedAt;


}
