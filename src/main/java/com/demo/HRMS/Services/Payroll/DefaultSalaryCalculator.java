package com.demo.HRMS.Services.Payroll;

import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.PayslipEntity;
import com.demo.HRMS.Repositories.LeaveRequestRepository;
import com.demo.HRMS.Repositories.LeaveSheetRepository;
import com.demo.HRMS.Types.EmploymentType;
import com.demo.HRMS.Types.LeaveRequestStatus;
import com.demo.HRMS.Types.PayType;
import com.demo.HRMS.Types.PayslipStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DefaultSalaryCalculator  {


    @Autowired
    private LeaveRequestRepository leaveRequestRepository;


    private static final BigDecimal MONTHLY_DAYS = BigDecimal.valueOf(30);


    private static final BigDecimal HOURS_PER_DAY = BigDecimal.valueOf(8);


    public PayslipEntity calculate(EmployeeEntity employee,
                                   EmployeeCompensationEntity comp,
                                   LocalDate periodStart,
                                   LocalDate periodEnd,int leave) {

        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException("periodStart and periodEnd are required");
        }
        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException("periodEnd cannot be before periodStart");
        }

        int totalDays = (int) ChronoUnit.DAYS.between(periodStart, periodEnd) + 1;
        int paidDays  = calculatePaidDays(employee, periodStart, periodEnd)-leave;


        BigDecimal basic   = nz(comp.getBasicSalary());
        BigDecimal hra     = nz(comp.getHra());
        BigDecimal stipend = nz(comp.getStipend());
        BigDecimal hourly  = nz(comp.getHourlyRate());
        BigDecimal pfPct   = nz(comp.getPfPercentage());

        BigDecimal gross;
        BigDecimal hourlyHours = null;


        switch (comp.getPayType()) {
            case HOURLY -> {
                BigDecimal hours = HOURS_PER_DAY.multiply(BigDecimal.valueOf(paidDays));
                hourlyHours = hours;
                gross = hourly.multiply(hours);
            }
            case STIPEND -> gross = monthlyProrate(stipend, paidDays);
            case SALARY  -> gross = monthlyProrate(basic.add(hra), paidDays);
            default      -> gross = BigDecimal.ZERO;
        }

        EmploymentType type = employee.getEmpType();
        if (type == EmploymentType.PROJECT_TRAINEE
                && comp.getPayType() != PayType.STIPEND) {
            gross = monthlyProrate(stipend, paidDays);
        } else if (type == EmploymentType.CONTRACT
                && comp.getPayType() != PayType.HOURLY) {
            gross = monthlyProrate(basic.add(hra), paidDays);
        }

        BigDecimal pfAmount = BigDecimal.ZERO;
        if (type == EmploymentType.FULL_TIME && comp.getPayType() == PayType.SALARY && pfPct.compareTo(BigDecimal.ZERO) > 0) {
            pfAmount = gross.multiply(pfPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal netPay = gross.subtract(pfAmount).setScale(2, RoundingMode.HALF_UP);
        gross = gross.setScale(2, RoundingMode.HALF_UP);

        return PayslipEntity.builder()
                .employee(employee)
                .department(employee.getDepartment())
                .organisation(employee.getOrganisation())
                .employmentType(type)
                .payType(comp.getPayType())
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .totalDays(totalDays)
                .paidDays(paidDays)
                .basicSalary(basic)
                .hra(hra)
                .stipend(stipend)
                .hourlyRate(hourly)
                .hourlyHours(hourlyHours)
                .grossPay(gross)
                .pfPercentage(pfPct)
                .pfAmount(pfAmount)
                .netPay(netPay)
                .status(PayslipStatus.SUBMITTED)
                .build();
    }


    private int calculatePaidDays(EmployeeEntity emp, LocalDate start, LocalDate end) {
        LocalDate effectiveStart = start.isBefore(emp.getEmpJoiningDate()) ? emp.getEmpJoiningDate() : start;

        if (end.isBefore(effectiveStart)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(effectiveStart, end) + 1;
    }


    private BigDecimal monthlyProrate(BigDecimal monthly, int paidDays) {
        if (monthly == null || monthly.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (paidDays <= 0) {
            return BigDecimal.ZERO;
        }
        return monthly
                .multiply(BigDecimal.valueOf(paidDays))
                .divide(MONTHLY_DAYS, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}