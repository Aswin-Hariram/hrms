package com.demo.HRMS.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "EmployeeLeaveData",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"empID","orgID", "leaveID"}
                )
        }
)
public class LeaveSheetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empLeaveId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empID", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgID", nullable = false)
    private OrganisationEntity organisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leaveID", nullable = false)
    @JsonBackReference
    private LeaveTypeEntity leaveType;

    @Column(nullable = false)
    private int allocatedDays;

    @Column(nullable = false)
    private int usedDays=0;

    @Column(nullable = false)
    private int remainingDays=0;
}