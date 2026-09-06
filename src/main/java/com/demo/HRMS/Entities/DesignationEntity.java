package com.demo.HRMS.Entities;



import com.demo.HRMS.Entities.OrganisationEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name= "Designations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesignationEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private Long designationId;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String designationName;


    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name="orgID", nullable = false)
    private OrganisationEntity organisation;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

