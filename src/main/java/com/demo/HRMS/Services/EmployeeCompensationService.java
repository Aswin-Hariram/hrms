package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Employee.EmployeeCompensationDTO;
import com.demo.HRMS.DTO.Employee.Response.CompensationHistoryResponseDTO;
import com.demo.HRMS.DTO.Employee.Response.CompensationResponseDTO;
import com.demo.HRMS.DTO.Employee.Response.ReporteeCompensationDTO;
import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Repositories.EmployeeCompensationRepository;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Types.EmploymentType;
import com.demo.HRMS.Types.PayType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeCompensationService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeCompensationRepository compensationRepository;

    private final HierarchyService hierarchyService;

    public EmployeeCompensationService(EmployeeRepository employeeRepository,
                                       EmployeeCompensationRepository compensationRepository,
                                       HierarchyService hierarchyService) {
        this.employeeRepository = employeeRepository;
        this.compensationRepository = compensationRepository;
        this.hierarchyService = hierarchyService;
    }

    private CompensationResponseDTO toDto(EmployeeCompensationEntity c) {
        if (c == null) return null;

        return CompensationResponseDTO.builder()
                .compensationId(c.getCompensationId())
                .empID(c.getEmployee().getEmpID())
                .employeeName(
                        c.getEmployee().getEmpFirstName() + " "
                                + c.getEmployee().getEmpLastName()
                )
                .payType(c.getPayType())
                .hourlyRate(c.getHourlyRate())
                .stipend(c.getStipend())
                .basicSalary(c.getBasicSalary())
                .pfPercentage(c.getPfPercentage())
                .effectiveFrom(c.getEffectiveFrom())
                .active(c.isActive())
                .revisionReason(c.getRevisionReason())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private void validatePayType(EmploymentType employmentType, PayType payType) {

        if (employmentType == EmploymentType.CONTRACT
                && payType != PayType.HOURLY) {
            throw new RuntimeException("Contract employee must have hourly pay");
        }

        if (employmentType == EmploymentType.PROJECT_TRAINEE
                && payType != PayType.STIPEND) {
            throw new RuntimeException("Project trainee must have stipend");
        }

        if (employmentType == EmploymentType.FULL_TIME
                && payType != PayType.SALARY) {
            throw new RuntimeException("Full-time employee must have salary");
        }
    }

    private void validateCompensation(EmployeeCompensationDTO dto) {

        if (dto.getEffectiveFrom() == null) {
            throw new RuntimeException("Effective from date is required");
        }

        switch (dto.getPayType()) {
            case HOURLY -> {
                if (dto.getHourlyRate() == null) {
                    throw new RuntimeException("Hourly rate is required");
                }
            }
            case STIPEND -> {
                if (dto.getStipend() == null) {
                    throw new RuntimeException("Stipend is required");
                }
            }
            case SALARY -> {
                if (dto.getBasicSalary() == null) {
                    throw new RuntimeException("Basic salary is required");
                }
                if (dto.getPfPercentage() == null) {
                    throw new RuntimeException("PF percentage is required");
                }
            }
        }
    }
    @Transactional
    public Map<String, Object> addOrUpdateCompensation(
            Long empID,
            Long orgId,
            EmployeeCompensationDTO dto
    ) {

        EmployeeEntity employee = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(empID, orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        validatePayType(employee.getEmpType(), dto.getPayType());
        validateCompensation(dto);

        // Deactivate the current active revision (if any)
        Optional<EmployeeCompensationEntity> existing =
                compensationRepository.findByEmployee_EmpIDAndActiveTrue(empID);

        existing.ifPresent(current -> {
            if (!dto.getEffectiveFrom().isAfter(current.getEffectiveFrom())) {
                throw new RuntimeException(
                        "Effective date must be after the current active compensation's effective date ("
                                + current.getEffectiveFrom() + ")"
                );
            }
            current.setActive(false);
            compensationRepository.save(current);
        });


        EmployeeCompensationEntity revision = EmployeeCompensationEntity.builder()
                .employee(employee)
                .payType(dto.getPayType())
                .hourlyRate(dto.getHourlyRate())
                .stipend(dto.getStipend())
                .basicSalary(dto.getBasicSalary())
                .pfPercentage(dto.getPfPercentage())
                .effectiveFrom(dto.getEffectiveFrom())
                .revisionReason(dto.getRevisionReason())
                .active(true)
                .build();

        compensationRepository.save(revision);

        boolean wasUpdate = existing.isPresent();

        return Map.of(
                "message", wasUpdate
                        ? "Compensation revised successfully"
                        : "Compensation added successfully",
                "employeeId", empID,
                "compensationId", revision.getCompensationId(),
                "data", toDto(revision)
        );
    }


    @Transactional
    public Map<String, Object> getReporteeCompensations(Long hrEmpId, Long orgId) {

        employeeRepository.findByEmpIDAndOrganisation_OrgID(hrEmpId, orgId)
                .orElseThrow(() -> new RuntimeException("HR not found"));


        Set<Long> descendantIds = hierarchyService.getDescendantIds(hrEmpId, orgId);

        if (descendantIds.isEmpty()) {
            return Map.of("message", "Success", "count", 0, "data", List.of());
        }

        List<EmployeeEntity> reportees =
                employeeRepository.findAllById(descendantIds);

        List<EmployeeCompensationEntity> compensations = compensationRepository
                .findAllByEmployee_EmpIDInAndActiveTrue(new ArrayList<>(descendantIds));

        Map<Long, EmployeeCompensationEntity> compByEmpId = compensations.stream()
                .collect(Collectors.toMap(c -> c.getEmployee().getEmpID(), c -> c));

        List<ReporteeCompensationDTO> response = new ArrayList<>();
        for (EmployeeEntity emp : reportees) {
            EmployeeCompensationEntity comp = compByEmpId.get(emp.getEmpID());

            ReporteeCompensationDTO dto = ReporteeCompensationDTO.builder()
                    .empID(emp.getEmpID())
                    .employeeName(emp.getEmpFirstName() + " " + emp.getEmpLastName())
                    .employeeEmail(emp.getEmpEmail())
                    .hasCompensation(comp != null)
                    .build();

            if (comp != null) {
                dto.setPayType(comp.getPayType());
                dto.setHourlyRate(comp.getHourlyRate());
                dto.setStipend(comp.getStipend());
                dto.setBasicSalary(comp.getBasicSalary());
                dto.setPfPercentage(comp.getPfPercentage());
                dto.setEffectiveFrom(comp.getEffectiveFrom());
            }

            response.add(dto);
        }

        return Map.of("message", "Success", "count", response.size(), "data", response);
    }


    @Transactional
    public Map<String, Object> getActiveCompensation(Long empID, Long orgId) {

        employeeRepository.findByEmpIDAndOrganisation_OrgID(empID, orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeCompensationEntity active = compensationRepository
                .findByEmployee_EmpIDAndActiveTrue(empID)
                .orElseThrow(() -> new RuntimeException(
                        "No active compensation found for employee " + empID
                ));

        return Map.of(
                "message", "Success",
                "data", toDto(active)
        );
    }


    @Transactional
    public Map<String, Object> getCompensationHistory(Long empID, Long orgId) {

        EmployeeEntity employee = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(empID, orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<EmployeeCompensationEntity> all =
                compensationRepository.findHistoryByEmployeeId(empID);

        CompensationResponseDTO activeDto = null;
        List<CompensationResponseDTO> previous = new ArrayList<>();

        for (EmployeeCompensationEntity c : all) {
            CompensationResponseDTO dto = toDto(c);
            if (c.isActive()) {
                activeDto = dto;
            } else {
                previous.add(dto);
            }
        }

        CompensationHistoryResponseDTO response =
                CompensationHistoryResponseDTO.builder()
                        .empID(employee.getEmpID())
                        .employeeName(
                                employee.getEmpFirstName() + " "
                                        + employee.getEmpLastName()
                        )
                        .totalRevisions(all.size())
                        .active(activeDto)
                        .previousRevisions(previous)
                        .build();

        return Map.of(
                "message", "Success",
                "data", response
        );
    }
}