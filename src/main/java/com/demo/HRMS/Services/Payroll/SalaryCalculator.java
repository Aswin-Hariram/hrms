package com.demo.HRMS.Services.Payroll;

import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.PayslipEntity;
import com.demo.HRMS.Types.EmploymentType;

import java.time.LocalDate;

public interface SalaryCalculator {

    PayslipEntity calculate(
            EmployeeEntity employee,
            EmployeeCompensationEntity compensation,
            LocalDate periodStart,
            LocalDate periodEnd,
            int leaveDays
    );
}