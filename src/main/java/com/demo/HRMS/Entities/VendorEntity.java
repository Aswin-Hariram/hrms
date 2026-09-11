package com.demo.HRMS.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "vendors",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vendor_org_email",
                        columnNames = {"org_id", "email"}
                ),
                @UniqueConstraint(
                        name = "uk_vendor_org_gst",
                        columnNames = {"org_id", "gst_number"}
                ),
                @UniqueConstraint(
                        name = "uk_vendor_org_name",
                        columnNames = {"org_id", "vendor_name"}
                )
        }
)
public class VendorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "org_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_vendor_org")
    )
    private OrganisationEntity organisation;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}