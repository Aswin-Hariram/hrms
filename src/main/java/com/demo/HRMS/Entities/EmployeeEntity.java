package com.demo.HRMS.Entities;

import com.demo.HRMS.Types.EmployeeRole;
import com.demo.HRMS.Types.EmploymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "Employee",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_emp_email_org",
                columnNames = {"empEmail", "orgID"}
        )
)
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgID", nullable = false)
    private OrganisationEntity organisation;

    @Column(nullable = false)
    private String empFirstName;

    @Column(nullable = false)
    private String empLastName;

    @Column(nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType empType;

    @Column(nullable = false)
    private String empStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportToHr")
    private EmployeeEntity reportToHr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designationId")
    private DesignationEntity designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId")
    private DepartmentEntity department;


    @OneToMany(
            mappedBy = "employee",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @Builder.Default
    private List<EmployeeCompensationEntity> compensations = new ArrayList<>();

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

    @Builder.Default
    private boolean isActiveLeaveRequest = false;
}