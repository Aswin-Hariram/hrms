package com.demo.HRMS.Services.Payroll;

import com.demo.HRMS.Types.EmploymentType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SalaryCalculatorFinder {

    private final Map<EmploymentType, SalaryCalculator> calculators;

    public SalaryCalculatorFinder(
            FullTimeSalaryCalculator fullTimeSalaryCalculator,
            ContractSalaryCalculator contractSalaryCalculator,
            TraineeSalaryCalculator traineeSalaryCalculator) {

        this.calculators = Map.of(
                EmploymentType.FULL_TIME, fullTimeSalaryCalculator,
                EmploymentType.CONTRACT, contractSalaryCalculator,
                EmploymentType.PROJECT_TRAINEE, traineeSalaryCalculator
        );
    }

    public SalaryCalculator getCalculator(
            EmploymentType employmentType) {

        SalaryCalculator calculator = calculators.get(employmentType);

        if (calculator == null) {
            throw new IllegalArgumentException(
                    "No salary calculator configured for employment type: "
                            + employmentType
            );
        }

        return calculator;
    }
}