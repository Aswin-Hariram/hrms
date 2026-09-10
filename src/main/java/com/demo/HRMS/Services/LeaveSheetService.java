package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.EmployeesLeaveData.CreateLeaveSheetDTO;
import com.demo.HRMS.DTO.LeaveSheet.Response.LeaveSheetResponseDTO;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.LeaveSheetEntity;
import com.demo.HRMS.Entities.LeaveTypeEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.LeaveSheetRepository;
import com.demo.HRMS.Repositories.LeaveTypeRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LeaveSheetService {

    @Autowired
    private LeaveSheetRepository leaveSheetRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Transactional
    public Map<String, Object> getLeaves(Long orgId) {

        if (!organisationRepository.existsById(orgId)) {
            throw new RuntimeException("Organisation not found");
        }

        List<LeaveSheetEntity> leaves =
                leaveSheetRepository.findAllByOrganisation_OrgID(orgId);

        List<LeaveSheetResponseDTO> response = new ArrayList<>();
        for (LeaveSheetEntity leaveSheet : leaves) {
            response.add(
                    LeaveSheetResponseDTO.builder()
                            .leaveID(leaveSheet.getLeaveType().getLeaveId())
                            .leaveName(leaveSheet.getLeaveType().getLeaveName())
                            .allocatedDays(leaveSheet.getAllocatedDays())
                            .usedDays(leaveSheet.getUsedDays())
                            .remainingDays(leaveSheet.getRemainingDays())
                            .build()
            );
        }

        return Map.of(
                "message", "success",
                "data", response
        );
    }

    @Transactional
    public Map<String, Object> createLeave(CreateLeaveSheetDTO request) {

        if (request.getLeaveIDs() == null || request.getLeaveIDs().isEmpty()) {
            throw new RuntimeException("At least one leave ID is required");
        }

        Long loggedEmpId = Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );


        EmployeeEntity loggedEmployee = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(loggedEmpId, request.getOrgID())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        Long orgId = loggedEmployee.getOrganisation().getOrgID();

        EmployeeEntity employee = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(request.getEmpID(), orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Super Admin bypass; otherwise must be the employee's direct report-to HR
        boolean isSuperAdmin = loggedEmployee.getEmpRole().name().equals("SUPER_ADMIN");
        boolean isDirectReportTo = employee.getReportToHr() != null
                && Objects.equals(employee.getReportToHr().getEmpID(), loggedEmpId);

        if (!isSuperAdmin && !isDirectReportTo) {
            throw new RuntimeException("You are not permitted to access this employee");
        }

        for (Long leaveId : request.getLeaveIDs()) {

            LeaveTypeEntity leaveType = leaveTypeRepository
                    .findByOrganisation_OrgIDAndLeaveId(orgId, leaveId)
                    .orElseThrow(() -> new RuntimeException("Invalid leave id " + leaveId));

            boolean alreadyExists =
                    leaveSheetRepository
                            .existsByEmployee_EmpIDAndOrganisation_OrgIDAndLeaveType_LeaveId(
                                    employee.getEmpID(),
                                    orgId,
                                    leaveId
                            );

            if (alreadyExists) {
                throw new RuntimeException("Leave sheet already exists for leave id " + leaveId);
            }

            LeaveSheetEntity sheetEntity = LeaveSheetEntity.builder()
                    .employee(employee)
                    .organisation(employee.getOrganisation())
                    .leaveType(leaveType)
                    .allocatedDays(leaveType.getNoDays())
                    .usedDays(0)
                    .remainingDays(leaveType.getNoDays())
                    .build();

            leaveSheetRepository.save(sheetEntity);
        }

        return Map.of("message", "success");
    }
}