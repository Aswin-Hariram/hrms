package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Employee.EmployeeCompensationDTO;
import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Repositories.EmployeeCompensationRepository;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Types.EmploymentType;
import com.demo.HRMS.Types.PayType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmployeeCompensationService {


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeCompensationRepository compensationRepository;



    private void validatePayType(
            EmploymentType employmentType,
            PayType payType
    ) {

        if (employmentType == EmploymentType.CONTRACT
                && payType != PayType.HOURLY) {

            throw new RuntimeException(
                    "Contract employee must have hourly pay"
            );
        }

        if (employmentType == EmploymentType.PROJECT_TRAINEE
                && payType != PayType.STIPEND) {

            throw new RuntimeException(
                    "Project trainee must have stipend"
            );
        }

        if (employmentType == EmploymentType.FULL_TIME
                && payType != PayType.SALARY) {

            throw new RuntimeException(
                    "Full-time employee must have salary"
            );
        }
    }
    private void validateCompensation(EmployeeCompensationDTO dto) {

        switch (dto.getPayType()) {

            case HOURLY -> {

                if (dto.getHourlyRate() == null) {
                    throw new RuntimeException(
                            "Hourly rate is required"
                    );
                }
            }

            case STIPEND -> {

                if (dto.getStipend() == null) {
                    throw new RuntimeException(
                            "Stipend is required"
                    );
                }
            }

            case SALARY -> {

                if (dto.getBasicSalary() == null) {
                    throw new RuntimeException(
                            "Basic salary is required"
                    );
                }

                if (dto.getPfPercentage() == null) {
                    throw new RuntimeException(
                            "PF percentage is required"
                    );
                }
            }
        }
    }

    @Transactional
    public Map<String, Object> addCompensation(
            Long empID,
            EmployeeCompensationDTO dto
    ) {

        EmployeeEntity employee = employeeRepository.findById(empID)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found")
                );

        validatePayType(employee.getEmpType(), dto.getPayType());

        validateCompensation(dto);

        EmployeeCompensationEntity compensation =
                EmployeeCompensationEntity.builder()
                        .employee(employee)
                        .payType(dto.getPayType())
                        .hourlyRate(dto.getHourlyRate())
                        .stipend(dto.getStipend())
                        .basicSalary(dto.getBasicSalary())
                        .pfPercentage(dto.getPfPercentage())
                        .effectiveFrom(dto.getEffectiveFrom())
                        .active(true)
                        .build();

        compensationRepository.save(compensation);

        return Map.of(
                "message", "Employee compensation added successfully",
                "employeeId", empID,
                "payType", dto.getPayType()
        );
    }

}
