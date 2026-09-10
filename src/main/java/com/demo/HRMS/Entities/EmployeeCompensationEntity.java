package com.demo.HRMS.Entities;

import com.demo.HRMS.Types.PayType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "EmployeeCompensation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCompensationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long compensationId;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "empID",
            nullable = false,
            unique = true
    )
    private EmployeeEntity employee;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayType payType;



    @Column(precision = 12, scale = 2)
    private BigDecimal hourlyRate;


    @Column(precision = 12, scale = 2)
    private BigDecimal stipend;



    @Column(precision = 12, scale = 2)
    private BigDecimal basicSalary;



    @Column(precision = 5, scale = 2)
    private BigDecimal pfPercentage;


    @Column(nullable = false)
    private LocalDate effectiveFrom;


    @Column(nullable = false)
    private boolean active = true;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}