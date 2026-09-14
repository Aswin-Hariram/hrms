package com.demo.HRMS.Services.Payroll;

import com.demo.HRMS.Entities.EmployeeEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class PayrollCalculationUtils {

    private PayrollCalculationUtils() {
        // Utility class - prevent instantiation
    }

    public static final BigDecimal MONTHLY_DAYS =
            BigDecimal.valueOf(30);


    public static int calculatePaidDays(
            EmployeeEntity employee,
            LocalDate start,
            LocalDate end,
            int leaveDays) {

        validate(start, end);

        if (employee == null) {
            throw new IllegalArgumentException(
                    "Employee is required"
            );
        }

        if (employee.getEmpJoiningDate() == null) {
            throw new IllegalArgumentException(
                    "Employee joining date is required"
            );
        }

        if (leaveDays < 0) {
            throw new IllegalArgumentException(
                    "Leave days cannot be negative"
            );
        }

        LocalDate effectiveStart =
                start.isBefore(employee.getEmpJoiningDate())
                        ? employee.getEmpJoiningDate()
                        : start;

        if (end.isBefore(effectiveStart)) {
            return 0;
        }

        int payableDays = (int) ChronoUnit.DAYS.between(effectiveStart, end) + 1;

        return Math.max(payableDays - leaveDays, 0);
    }

    public static BigDecimal monthlyProrate(
            BigDecimal monthly,
            int paidDays) {

        monthly = nz(monthly);

        if (paidDays <= 0 ||
                monthly.compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        return monthly
                .multiply(BigDecimal.valueOf(paidDays))
                .divide(
                        MONTHLY_DAYS,
                        4,
                        RoundingMode.HALF_UP
                );
    }


    public static BigDecimal nz(BigDecimal value) {
        return value == null
                ? BigDecimal.ZERO
                : value;
    }


    public static BigDecimal money(BigDecimal value) {
        return nz(value)
                .setScale(2, RoundingMode.HALF_UP);
    }


    public static void validate(LocalDate start, LocalDate end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    "periodStart and periodEnd are required"
            );
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException(
                    "periodEnd cannot be before periodStart"
            );
        }
    }


    public static int calculateTotalDays(LocalDate start, LocalDate end) {

        validate(start, end);

        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }
}