package com.demo.HRMS.Entities;

import com.demo.HRMS.Types.EmploymentType;
import com.demo.HRMS.Types.PayType;
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
                name = "uk_payslip_org_emp_period",
                columnNames = {"org_id", "empID", "period_start", "period_end"}
        ),
        indexes = {
                @Index(name = "idx_payslip_org_emp",  columnList = "org_id, empID"),
                @Index(name = "idx_payslip_org_dept", columnList = "org_id, department_id"),
                @Index(name = "idx_payslip_org_status", columnList = "org_id, status"),
                @Index(name = "idx_payslip_org_period_end", columnList = "org_id, period_end")
        }
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
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganisationEntity organisation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayType payType;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(name = "paid_days", nullable = false)
    private int paidDays;

    @Column(name = "unpaidLeaveDays", nullable = false)
    private int unpaidLeaveDays;

    @Builder.Default
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal basicSalary = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal hra = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal stipend = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    @Column(name = "hourly_hours", precision = 8, scale = 2)
    private BigDecimal hourlyHours;

    @Builder.Default
    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal grossPay = BigDecimal.ZERO;


    @Column(name = "pf_percentage", precision = 5, scale = 2)
    private BigDecimal pfPercentage;

    @Builder.Default
    @Column(name = "pf_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal pfAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "net_pay", precision = 14, scale = 2, nullable = false)
    private BigDecimal netPay = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayslipStatus status = PayslipStatus.SUBMITTED;

    @Column(name = "approved_by")
    private Long approvedByEmpId;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "paid_by")
    private Long paidByEmpId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_mode", length = 30)
    private String paymentMode;         // NEFT / IMPS / UPI / CASH

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;    // UTR / cheque no / UPI ref

    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;
}