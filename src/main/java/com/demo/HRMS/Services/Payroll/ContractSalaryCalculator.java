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
public class ContractSalaryCalculator implements SalaryCalculator {

    private static final BigDecimal HOURS_PER_DAY =
            BigDecimal.valueOf(8);

    @Override
    public PayslipEntity calculate(
            EmployeeEntity employee,
            EmployeeCompensationEntity comp,
            LocalDate periodStart,
            LocalDate periodEnd,
            int leaveDays) {

        PayrollCalculationUtils.validate(
                periodStart,
                periodEnd
        );

        if (employee.getEmpType() != EmploymentType.CONTRACT) {
            throw new IllegalArgumentException(
                    "ContractSalaryCalculator can only calculate CONTRACT employees"
            );
        }

        if (comp.getPayType() != PayType.HOURLY) {
            throw new IllegalArgumentException(
                    "Contract employee must have HOURLY pay type"
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

        BigDecimal hourlyRate = PayrollCalculationUtils.nz(comp.getHourlyRate());

        BigDecimal hourlyHours = HOURS_PER_DAY.multiply(
                        BigDecimal.valueOf(paidDays)
                );

        BigDecimal grossPay =
                PayrollCalculationUtils.money(
                        hourlyRate.multiply(hourlyHours)
                );

        return PayslipEntity.builder()
                .employee(employee)
                .department(employee.getDepartment())
                .organisation(employee.getOrganisation())

                .employmentType(
                        EmploymentType.CONTRACT
                )

                .payType(
                        PayType.HOURLY
                )

                .periodStart(periodStart)
                .periodEnd(periodEnd)

                .totalDays(totalDays)
                .paidDays(paidDays)

                .basicSalary(BigDecimal.ZERO)
                .hra(BigDecimal.ZERO)
                .stipend(BigDecimal.ZERO)

                .hourlyRate(hourlyRate)
                .hourlyHours(hourlyHours)

                .grossPay(grossPay)

                .pfPercentage(BigDecimal.ZERO)
                .pfAmount(BigDecimal.ZERO)

                .netPay(grossPay)

                .status(PayslipStatus.SUBMITTED)

                .build();
    }
}