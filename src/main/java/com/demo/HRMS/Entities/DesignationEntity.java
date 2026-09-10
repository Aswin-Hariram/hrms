package com.demo.HRMS.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Designations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designationId;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String designationName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orgID", nullable = false)
    private OrganisationEntity organisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departmentId", nullable = false)
    private DepartmentEntity department;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}