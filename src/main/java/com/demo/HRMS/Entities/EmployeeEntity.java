package com.demo.HRMS.Entities;

import com.demo.HRMS.Types.EmployeeAuthorities;
import com.demo.HRMS.Types.EmployeeRole;
import com.demo.HRMS.Types.EmployeeStatus;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                columnNames = {"emp_email", "org_id"}
        )
)
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long empID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganisationEntity organisation;

    @Column(name = "emp_first_name", nullable = false)
    private String empFirstName;

    @Column(name = "emp_last_name", nullable = false)
    private String empLastName;

    @Column(name = "emp_email", nullable = false)
    private String empEmail;

    @Column(name = "emp_phone_number", nullable = false)
    private String empPhoneNumber;

    @Column(name = "emp_password", nullable = false)
    private String empPassword;

    @Column(name = "emp_dob", nullable = false)
    @Past(message = "Date of Birth must be a date from past")
    private LocalDate empDOB;

    @Column(name = "age", nullable = false)
    @Max(60)
    @Min(18)
    private int age;

    @Column(name = "emp_joining_date", nullable = false)
    private LocalDate empJoiningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "emp_type", nullable = false)
    private EmploymentType empType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "employee_authorities",
            joinColumns = @JoinColumn(name = "emp_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    @Builder.Default
    private Set<EmployeeAuthorities> authorities = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "emp_status", nullable = false, length = 20)
    private EmployeeStatus empStatus = EmployeeStatus.INACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_to_hr")
    private EmployeeEntity reportToHr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private DesignationEntity designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @OneToMany(
            mappedBy = "employee",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @Builder.Default
    private List<EmployeeCompensationEntity> compensations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "emp_role", nullable = false)
    private EmployeeRole empRole;

    @Column(name = "default_password_updated", nullable = false)
    private boolean defaultPasswordUpdated = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_active_leave_request", nullable = false)
    private boolean isActiveLeaveRequest = false;
}