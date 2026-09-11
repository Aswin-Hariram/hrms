package com.demo.HRMS.Entities;

import com.demo.HRMS.Types.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
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
        name = "vendor_invoices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invoice_org_number",
                        columnNames = {"org_id", "invoice_number"}
                )
        }
)
public class VendorInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganisationEntity organisation;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorEntity vendor;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private VendorEntity contract;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;


    @Column(name = "sub_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;


    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;


    @Column(name = "paid_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;


    @Column(name = "balance_amount", precision = 15, scale = 2)
    private BigDecimal balanceAmount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "INR";


    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;


//    @Column(name = "document_url", length = 500)
//    private String documentUrl;


    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VendorPayment> payments = new ArrayList<>();


    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @PrePersist
    @PreUpdate
    private void computeDerived() {
        if (subTotal == null) subTotal = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;

        this.totalAmount = subTotal
                .add(taxAmount)
                .subtract(discountAmount);

        if (paidAmount == null) paidAmount = BigDecimal.ZERO;
        this.balanceAmount = this.totalAmount.subtract(paidAmount);


        if (balanceAmount.compareTo(BigDecimal.ZERO) > 0
                && dueDate != null
                && dueDate.isBefore(LocalDate.now())
                && status != InvoiceStatus.PAID
                && status != InvoiceStatus.CANCELLED
                && status != InvoiceStatus.REJECTED) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }
}