package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Vendor.CreateVendorInvoiceDTO;
import com.demo.HRMS.DTO.Vendor.VendorInvoiceResponse;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Entities.VendorEntity;
import com.demo.HRMS.Entities.VendorInvoice;
import com.demo.HRMS.Repositories.OrganisationRepository;
import com.demo.HRMS.Repositories.VendorInvoiceRepository;
import com.demo.HRMS.Repositories.VendorRepository;
import com.demo.HRMS.Types.InvoiceStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorInvoiceService {

    @Autowired private VendorInvoiceRepository invoiceRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private OrganisationRepository organisationRepository;

    @Transactional
    public VendorInvoiceResponse createInvoice(CreateVendorInvoiceDTO request,
                                               Long orgId,
                                               Long userId) {

        OrganisationEntity organisation = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        VendorEntity vendor = vendorRepository
                .findByIdAndOrganisation_OrgID(request.getVendorId(), orgId)
                .orElseThrow(() -> new RuntimeException("Vendor not found in this organisation"));

        if (Boolean.FALSE.equals(vendor.getIsActive())) {
            throw new RuntimeException("Cannot create invoice for an inactive vendor");
        }

        if (invoiceRepository.existsByOrganisation_OrgIDAndInvoiceNumber(
                orgId, request.getInvoiceNumber())) {
            throw new RuntimeException("Invoice number already exists for this organisation");
        }

        if (request.getDueDate().isBefore(request.getInvoiceDate())) {
            throw new RuntimeException("Due date cannot be earlier than invoice date");
        }

        VendorInvoice invoice = VendorInvoice.builder()
                .organisation(organisation)
                .vendor(vendor)
                .invoiceNumber(request.getInvoiceNumber())
                .invoiceDate(request.getInvoiceDate())
                .dueDate(request.getDueDate())
                .subTotal(request.getSubTotal())
                .taxAmount(request.getTaxAmount() != null
                        ? request.getTaxAmount() : BigDecimal.ZERO)
                .discountAmount(request.getDiscountAmount() != null
                        ? request.getDiscountAmount() : BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .description(request.getDescription())
                .status(InvoiceStatus.DRAFT)
                .createdBy(userId)
                .build();

        invoice = invoiceRepository.save(invoice);
        return mapToResponse(invoice);
    }

    public VendorInvoiceResponse getInvoice(Long id, Long orgId) {
        VendorInvoice invoice = invoiceRepository
                .findByIdAndOrganisation_OrgID(id, orgId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return mapToResponse(invoice);
    }

    public List<VendorInvoiceResponse> listInvoices(Long orgId, Long vendorId) {
        List<VendorInvoice> invoices = (vendorId == null)
                ? invoiceRepository.findByOrganisation_OrgIDOrderByInvoiceDateDesc(orgId)
                : invoiceRepository.findByOrganisation_OrgIDAndVendorIdOrderByInvoiceDateDesc(orgId, vendorId);

        return invoices.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public VendorInvoiceResponse submitForApproval(Long id, Long orgId, Long userId) {
        VendorInvoice invoice = invoiceRepository
                .findByIdAndOrganisation_OrgID(id, orgId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT invoices can be submitted for approval");
        }

        invoice.setStatus(InvoiceStatus.PENDING_APPROVAL);
        invoice.setUpdatedBy(userId);
        invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }

    @Transactional
    public VendorInvoiceResponse approve(Long id, Long orgId, Long userId) {
        VendorInvoice invoice = invoiceRepository
                .findByIdAndOrganisation_OrgID(id, orgId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Only PENDING_APPROVAL invoices can be approved");
        }

        invoice.setStatus(InvoiceStatus.APPROVED);
        invoice.setApprovedBy(userId);
        invoice.setApprovedAt(LocalDateTime.now());
        invoice.setUpdatedBy(userId);
        invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }

    @Transactional
    public VendorInvoiceResponse reject(Long id, Long orgId, Long userId, String reason) {
        VendorInvoice invoice = invoiceRepository
                .findByIdAndOrganisation_OrgID(id, orgId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Only PENDING_APPROVAL invoices can be rejected");
        }
        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("Rejection reason is required");
        }

        invoice.setStatus(InvoiceStatus.REJECTED);
        invoice.setRejectionReason(reason);
        invoice.setUpdatedBy(userId);
        invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }

    private VendorInvoiceResponse mapToResponse(VendorInvoice inv) {
        return VendorInvoiceResponse.builder()
                .id(inv.getId())
                .orgId(inv.getOrganisation().getOrgID())
                .vendorId(inv.getVendor().getId())
                .vendorName(inv.getVendor().getVendorName())
                .invoiceNumber(inv.getInvoiceNumber())
                .invoiceDate(inv.getInvoiceDate())
                .dueDate(inv.getDueDate())
                .subTotal(inv.getSubTotal())
                .taxAmount(inv.getTaxAmount())
                .discountAmount(inv.getDiscountAmount())
                .totalAmount(inv.getTotalAmount())
                .paidAmount(inv.getPaidAmount())
                .balanceAmount(inv.getBalanceAmount())
                .currency(inv.getCurrency())
                .description(inv.getDescription())
                .status(inv.getStatus().name())
                .approvedBy(inv.getApprovedBy())
                .approvedAt(inv.getApprovedAt())
                .rejectionReason(inv.getRejectionReason())
                .createdBy(inv.getCreatedBy())
                .updatedBy(inv.getUpdatedBy())
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }
}