package com.demo.HRMS.Entities;

import com.demo.HRMS.Types.LeaveTypesCodes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
        name = "LeaveTypes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_leave_name_per_org",
                        columnNames = {"orgID", "leave_Name"}
                ),
                @UniqueConstraint(
                        name = "uq_leave_code_per_org",
                        columnNames = {"orgID", "leave_Code"}
                )
        }
)
@Builder
public class LeaveTypeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @ManyToOne
    @JoinColumn(name = "orgID", nullable = false)
    private OrganisationEntity organisation;

    @NotNull(message = "Leave name should be mentioned.")
    private String leaveName;


    @Min(value = 1, message = "Number of days must be at least 1")
    private int noDays;

    @NotNull(message = "Leave code should be mentioned.")
    @Column(name = "leave_Code", nullable = false, length = 20)
    private String leaveCode;

    @Column(nullable = false)
    private boolean isPaid;

    @Column(nullable = false)
    private boolean isApprovalRequired;

    @Column
    private boolean isActive;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

}
