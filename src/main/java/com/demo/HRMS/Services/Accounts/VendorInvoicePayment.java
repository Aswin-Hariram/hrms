package com.demo.HRMS.Services.Accounts;

import com.demo.HRMS.Entities.VendorInvoice;
import com.demo.HRMS.Entities.VendorPayment;
import com.demo.HRMS.Repositories.VendorInvoiceRepository;
import com.demo.HRMS.Repositories.VendorPaymentRepository;
import com.demo.HRMS.Types.InvoiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class VendorInvoicePayment implements Payment<VendorInvoice, VendorPayment> {

    private final VendorPaymentRepository paymentRepository;
    private final VendorInvoiceRepository invoiceRepository;

    @Override
    public VendorPayment pay(
            VendorInvoice invoice,
            BigDecimal amount,
            Long paidBy,
            String paymentMode,
            String paymentReference
    ) {

        if (invoice.getStatus() == InvoiceStatus.CANCELLED
                || invoice.getStatus() == InvoiceStatus.REJECTED) {

            throw new IllegalStateException(
                    "Cannot pay a " + invoice.getStatus() + " invoice"
            );
        }

        if (invoice.getStatus() == InvoiceStatus.DRAFT
                || invoice.getStatus() == InvoiceStatus.PENDING_APPROVAL) {

            throw new IllegalStateException(
                    "Invoice must be approved before payment"
            );
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException(
                    "Invoice is already fully paid"
            );
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be positive"
            );
        }

        BigDecimal alreadyPaid =
                paymentRepository.sumAmountByInvoiceId(invoice.getId());

        BigDecimal balance =
                invoice.getTotalAmount().subtract(alreadyPaid);

        if (amount.compareTo(balance) > 0) {
            throw new IllegalStateException(
                    "Payment amount exceeds outstanding balance of " + balance
            );
        }

        VendorPayment payment = VendorPayment.builder()
                .invoice(invoice)
                .paymentDate(LocalDate.now())
                .amount(amount)
                .paymentMode(paymentMode)
                .referenceNumber(paymentReference)
                .createdBy(paidBy)
                .build();

        payment = paymentRepository.save(payment);

        BigDecimal newPaid = alreadyPaid.add(amount);
        BigDecimal newBalance = invoice.getTotalAmount().subtract(newPaid);

        invoice.setPaidAmount(newPaid);
        invoice.setBalanceAmount(newBalance);
        invoice.setStatus(
                newBalance.compareTo(BigDecimal.ZERO) == 0
                        ? InvoiceStatus.PAID
                        : InvoiceStatus.PARTIALLY_PAID
        );
        invoice.setUpdatedBy(paidBy);

        invoiceRepository.save(invoice);

        return payment;
    }
}