package com.demo.HRMS.Services.Payroll;

import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.PayslipEntity;
import com.demo.HRMS.Types.EmploymentType;
import com.demo.HRMS.Types.PayType;
import com.demo.HRMS.Types.PayslipStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
public class FullTimeSalaryCalculator implements SalaryCalculator {

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

        if (employee.getEmpType() != EmploymentType.FULL_TIME) {
            throw new IllegalArgumentException(
                    "FullTimeSalaryCalculator can only calculate FULL_TIME employees"
            );
        }

        if (comp.getPayType() != PayType.SALARY) {
            throw new IllegalArgumentException(
                    "Full-time employee must have SALARY pay type"
            );
        }

        int totalDays =
                PayrollCalculationUtils.calculateTotalDays(
                        periodStart,
                        periodEnd
                );

        int paidDays = PayrollCalculationUtils.calculatePaidDays(
                        employee,
                        periodStart,
                        periodEnd,
                        leaveDays
                );

        BigDecimal basicSalary = PayrollCalculationUtils.nz(
                        comp.getBasicSalary()
                );

        BigDecimal hra = PayrollCalculationUtils.nz(
                        comp.getHra()
                );

        BigDecimal pfPercentage = PayrollCalculationUtils.nz(
                        comp.getPfPercentage()
                );

        BigDecimal monthlyGross =
                basicSalary.add(hra);

        BigDecimal grossPay =
                PayrollCalculationUtils.monthlyProrate(
                        monthlyGross,
                        paidDays
                );

        grossPay =
                PayrollCalculationUtils.money(grossPay);

        BigDecimal pfAmount = BigDecimal.ZERO;

        if (pfPercentage.compareTo(BigDecimal.ZERO) > 0) {
            pfAmount = grossPay
                    .multiply(pfPercentage)
                    .divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        pfAmount =
                PayrollCalculationUtils.money(pfAmount);

        BigDecimal netPay =
                PayrollCalculationUtils.money(
                        grossPay.subtract(pfAmount)
                );

        return PayslipEntity.builder()
                .employee(employee)
                .department(employee.getDepartment())
                .organisation(employee.getOrganisation())
                .employmentType(EmploymentType.FULL_TIME)
                .payType(PayType.SALARY)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .totalDays(totalDays)
                .paidDays(paidDays)
                .basicSalary(basicSalary)
                .hra(hra)
                .stipend(BigDecimal.ZERO)
                .hourlyRate(BigDecimal.ZERO)
                .hourlyHours(null)
                .grossPay(grossPay)
                .pfPercentage(pfPercentage)
                .pfAmount(pfAmount)
                .netPay(netPay)
                .status(PayslipStatus.SUBMITTED)
                .build();
    }
}