package com.demo.HRMS.Entities;

import com.demo.HRMS.Types.PayslipStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "Payslip",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_payslip_emp_month_year",
                columnNames = {"empID", "pay_month", "pay_year"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payslipId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empID", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orgID", nullable = false)
    private OrganisationEntity organisation;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compensationId")
    private EmployeeCompensationEntity compensation;

    @Column(name = "pay_month", nullable = false)
    private int payMonth;

    @Column(name = "pay_year", nullable = false)
    private int payYear;

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;


    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal grossEarnings = BigDecimal.ZERO;



    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal totalDeductions = BigDecimal.ZERO;


    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal netPay = BigDecimal.ZERO;


    @Column(nullable = false)
    private int totalWorkingDays = 0;

    @Column(nullable = false)
    private int daysPresent = 0;

    @Column(nullable = false)
    private int leavesTaken = 0;

    @Column(nullable = false)
    private int unpaidLeaves = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayslipStatus status = PayslipStatus.DRAFT;

    @Column(nullable = false)
    private LocalDate generatedDate;

    private LocalDate paidDate;

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}