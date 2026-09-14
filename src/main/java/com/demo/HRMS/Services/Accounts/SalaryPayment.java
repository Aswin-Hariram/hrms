package com.demo.HRMS.Services.Accounts;

import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.PayslipEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.PayslipRepository;
import com.demo.HRMS.Types.PayslipStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SalaryPayment implements Payment<PayslipEntity, PayslipEntity> {

    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public PayslipEntity pay(
            PayslipEntity payslip,
            BigDecimal amount,
            Long paidBy,
            String paymentMode,
            String paymentReference
    ) {

        if (payslip.getStatus() != PayslipStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only approved payslips can be paid"
            );
        }

        if (amount != null
                && payslip.getNetPay() != null
                && amount.compareTo(payslip.getNetPay()) != 0) {
            throw new IllegalArgumentException(
                    "Payslip payment amount must equal net pay"
            );
        }

        payslip.setStatus(PayslipStatus.PAID);
        payslip.setPaidByEmpId(paidBy);
        payslip.setPaidAt(LocalDateTime.now());
        payslip.setPaymentMode(paymentMode);
        payslip.setPaymentReference(paymentReference);

        payslipRepository.save(payslip);

        EmployeeEntity employee = payslip.getEmployee();

        LocalDate newLastSalaryPaidDate =
                employee.getLastSalaryPaidDate() == null
                        ? payslip.getPeriodEnd()
                        : payslip.getPeriodEnd()
                        .isAfter(employee.getLastSalaryPaidDate())
                        ? payslip.getPeriodEnd()
                        : employee.getLastSalaryPaidDate();

        employee.setLastSalaryPaidDate(newLastSalaryPaidDate);

        employeeRepository.save(employee);

        return payslip;
    }
}