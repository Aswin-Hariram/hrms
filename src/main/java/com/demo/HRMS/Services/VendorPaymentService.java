package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Vendor.CreateVendorPaymentDTO;
import com.demo.HRMS.DTO.Vendor.VendorPaymentResponse;
import com.demo.HRMS.Entities.VendorInvoice;
import com.demo.HRMS.Entities.VendorPayment;
import com.demo.HRMS.Repositories.VendorInvoiceRepository;
import com.demo.HRMS.Repositories.VendorPaymentRepository;
import com.demo.HRMS.Types.InvoiceStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorPaymentService {

    @Autowired private VendorPaymentRepository paymentRepository;
    @Autowired private VendorInvoiceRepository invoiceRepository;

    @Transactional
    public VendorPaymentResponse createPayment(CreateVendorPaymentDTO request,
                                               Long orgId,
                                               Long userId) {

        // Pessimistic lock prevents two concurrent payments from overpaying
        VendorInvoice invoice = invoiceRepository
                .findByIdAndOrgForUpdate(request.getInvoiceId(), orgId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.CANCELLED
                || invoice.getStatus() == InvoiceStatus.REJECTED) {
            throw new RuntimeException("Cannot add payment to a " + invoice.getStatus() + " invoice");
        }
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice is already fully paid");
        }
        if (invoice.getStatus() == InvoiceStatus.DRAFT
                || invoice.getStatus() == InvoiceStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Invoice must be approved before recording payments");
        }

        BigDecimal alreadyPaid = paymentRepository.sumAmountByInvoiceId(invoice.getId());
        BigDecimal balance = invoice.getTotalAmount().subtract(alreadyPaid);

        if (request.getAmount().compareTo(balance) > 0) {
            throw new RuntimeException(
                    "Payment amount exceeds outstanding balance of " + balance);
        }

        VendorPayment payment = VendorPayment.builder()
                .invoice(invoice)
                .paymentDate(request.getPaymentDate())
                .amount(request.getAmount())
                .paymentMode(request.getPaymentMode())
                .referenceNumber(request.getReferenceNumber())
                .remarks(request.getRemarks())
                .createdBy(userId)
                .build();

        payment = paymentRepository.save(payment);

        BigDecimal newPaid = alreadyPaid.add(request.getAmount());
        BigDecimal newBalance = invoice.getTotalAmount().subtract(newPaid);

        invoice.setPaidAmount(newPaid);
        invoice.setBalanceAmount(newBalance);
        invoice.setUpdatedBy(userId);

        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        return mapToResponse(payment, invoice);
    }

    public List<VendorPaymentResponse> getPaymentsForInvoice(Long invoiceId, Long orgId) {
        VendorInvoice invoice = invoiceRepository
                .findByIdAndOrganisation_OrgID(invoiceId, orgId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId)
                .stream()
                .map(p -> mapToResponse(p, invoice))
                .collect(Collectors.toList());
    }

    private VendorPaymentResponse mapToResponse(VendorPayment p, VendorInvoice invoice) {
        return VendorPaymentResponse.builder()
                .id(p.getId())
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .paymentDate(p.getPaymentDate())
                .amount(p.getAmount())
                .paymentMode(p.getPaymentMode())
                .referenceNumber(p.getReferenceNumber())
                .remarks(p.getRemarks())
                .invoiceTotalAmount(invoice.getTotalAmount())
                .invoicePaidAmount(invoice.getPaidAmount())
                .invoiceBalanceAmount(invoice.getBalanceAmount())
                .invoiceStatus(invoice.getStatus().name())
                .createdBy(p.getCreatedBy())
                .createdAt(p.getCreatedAt())
                .build();
    }
}