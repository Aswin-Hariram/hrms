package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Vendor.CreateVendorPaymentDTO;
import com.demo.HRMS.DTO.Vendor.VendorPaymentResponse;
import com.demo.HRMS.Entities.VendorInvoice;
import com.demo.HRMS.Entities.VendorPayment;
import com.demo.HRMS.Repositories.VendorInvoiceRepository;
import com.demo.HRMS.Repositories.VendorPaymentRepository;
import com.demo.HRMS.Services.Accounts.VendorInvoicePayment;
import com.demo.HRMS.Types.InvoiceStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorPaymentService {

    private final VendorPaymentRepository paymentRepository;
    private final VendorInvoiceRepository invoiceRepository;

    private final VendorInvoicePayment vendorInvoicePayment;


    @Transactional
    public VendorPaymentResponse createPayment(
            CreateVendorPaymentDTO request,
            Long orgId,
            Long userId
    ) {

        VendorInvoice invoice =
                invoiceRepository
                        .findByIdAndOrgForUpdate(
                                request.getInvoiceId(),
                                orgId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invoice not found"
                                )
                        );

        if (invoice.getStatus() == InvoiceStatus.CANCELLED
                || invoice.getStatus() == InvoiceStatus.REJECTED) {

            throw new RuntimeException(
                    "Cannot add payment to a "
                            + invoice.getStatus()
                            + " invoice"
            );
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {

            throw new RuntimeException(
                    "Invoice is already fully paid"
            );
        }

        if (invoice.getStatus() == InvoiceStatus.DRAFT
                || invoice.getStatus() == InvoiceStatus.PENDING_APPROVAL) {

            throw new RuntimeException(
                    "Invoice must be approved before recording payments"
            );
        }

        BigDecimal alreadyPaid = paymentRepository.sumAmountByInvoiceId(invoice.getId());

        BigDecimal balance = invoice.getTotalAmount().subtract(alreadyPaid);

        if (request.getAmount().compareTo(balance) > 0) {

            throw new RuntimeException(
                    "Payment amount exceeds outstanding balance of "
                            + balance
            );
        }


        VendorPayment payment = vendorInvoicePayment.pay(
                invoice,
                request.getAmount(),
                userId,
                request.getPaymentMode(),
                request.getReferenceNumber()
        );

        return mapToResponse(payment, invoice);
    }


    public List<VendorPaymentResponse> getPaymentsForInvoice(
            Long invoiceId,
            Long orgId
    ) {

        VendorInvoice invoice =
                invoiceRepository
                        .findByIdAndOrganisation_OrgID(
                                invoiceId,
                                orgId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invoice not found"
                                )
                        );

        return paymentRepository
                .findByInvoiceIdOrderByPaymentDateDesc(
                        invoiceId
                )
                .stream()
                .map(p ->
                        mapToResponse(
                                p,
                                invoice
                        )
                )
                .collect(Collectors.toList());
    }


    private VendorPaymentResponse mapToResponse(
            VendorPayment p,
            VendorInvoice invoice
    ) {

        return VendorPaymentResponse.builder()
                .id(p.getId())
                .invoiceId(invoice.getId())
                .invoiceNumber(
                        invoice.getInvoiceNumber()
                )
                .paymentDate(
                        p.getPaymentDate()
                )
                .amount(p.getAmount())
                .paymentMode(
                        p.getPaymentMode()
                )
                .referenceNumber(
                        p.getReferenceNumber()
                )
                .remarks(
                        p.getRemarks()
                )
                .invoiceTotalAmount(
                        invoice.getTotalAmount()
                )
                .invoicePaidAmount(
                        invoice.getPaidAmount()
                )
                .invoiceBalanceAmount(
                        invoice.getBalanceAmount()
                )
                .invoiceStatus(
                        invoice.getStatus().name()
                )
                .createdBy(
                        p.getCreatedBy()
                )
                .createdAt(
                        p.getCreatedAt()
                )
                .build();
    }
}