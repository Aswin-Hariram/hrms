package com.demo.HRMS.Services.Payroll;

import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.PayslipEntity;
import com.demo.HRMS.Types.EmploymentType;
import com.demo.HRMS.Types.PayType;
import com.demo.HRMS.Types.PayslipStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TraineeSalaryCalculator implements SalaryCalculator {

    @Override
    public PayslipEntity calculate(
            EmployeeEntity employee,
            EmployeeCompensationEntity comp,
            LocalDate periodStart,
            LocalDate periodEnd,
            int leaveDays) {

        PayrollCalculationUtils.validate(periodStart, periodEnd);

        if (employee.getEmpType() != EmploymentType.PROJECT_TRAINEE) {
            throw new IllegalArgumentException(
                    "TraineeSalaryCalculator can only calculate PROJECT_TRAINEE employees"
            );
        }

        if (comp.getPayType() != PayType.STIPEND) {
            throw new IllegalArgumentException(
                    "Project trainee must have STIPEND pay type"
            );
        }

        int totalDays = PayrollCalculationUtils.calculateTotalDays(
                        periodStart,
                        periodEnd
                );

        int paidDays = PayrollCalculationUtils.calculatePaidDays(
                        employee,
                        periodStart,
                        periodEnd,
                        leaveDays
                );

        BigDecimal stipend =
                PayrollCalculationUtils.nz(
                        comp.getStipend()
                );

        BigDecimal grossPay =
                PayrollCalculationUtils.money(
                        PayrollCalculationUtils.monthlyProrate(
                                stipend,
                                paidDays
                        )
                );

        return PayslipEntity.builder()
                .employee(employee)
                .department(employee.getDepartment())
                .organisation(employee.getOrganisation())

                .employmentType(
                        EmploymentType.PROJECT_TRAINEE
                )

                .payType(PayType.STIPEND)

                .periodStart(periodStart)
                .periodEnd(periodEnd)

                .totalDays(totalDays)
                .paidDays(paidDays)

                .basicSalary(BigDecimal.ZERO)
                .hra(BigDecimal.ZERO)

                .stipend(stipend)

                .hourlyRate(BigDecimal.ZERO)
                .hourlyHours(null)

                .grossPay(grossPay)

                .pfPercentage(BigDecimal.ZERO)
                .pfAmount(BigDecimal.ZERO)

                .netPay(grossPay)

                .status(PayslipStatus.SUBMITTED)

                .build();
    }
}