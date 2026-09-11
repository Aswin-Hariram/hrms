package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.LeaveRequest.Response.PendingLeaveResponse;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.LeaveRequestEntity;
import com.demo.HRMS.Entities.LeaveSheetEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.LeaveRequestRepository;
import com.demo.HRMS.Repositories.LeaveSheetRepository;
import com.demo.HRMS.Types.EmployeeRole;
import com.demo.HRMS.Types.LeaveRequestStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class LeaveRequestService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveSheetRepository leaveSheetRepository;
    private final HierarchyService hierarchyService;

    public LeaveRequestService(EmployeeRepository employeeRepository,
                               LeaveRequestRepository leaveRequestRepository,
                               LeaveSheetRepository leaveSheetRepository,
                               HierarchyService hierarchyService) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveSheetRepository = leaveSheetRepository;
        this.hierarchyService = hierarchyService;
    }


    @Transactional
    public Map<String, Object> getAllLeaveRequests(Long empId, Long orgId) {

        EmployeeEntity loggedEmp = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Long> descendantIds = hierarchyService.getDescendantIds(empId, orgId);

        List<LeaveRequestEntity> pending = descendantIds.isEmpty()
                ? List.of()
                : leaveRequestRepository
                .findAllByOrganisation_OrgIDAndEmployee_EmpIDInAndStatus(
                        orgId,
                        descendantIds,
                        LeaveRequestStatus.SUBMITTED
                );

        List<PendingLeaveResponse> response = pending.stream()
                .map(request -> PendingLeaveResponse.builder()
                        .requestID(request.getRequestID())
                        .employeeID(request.getEmployee().getEmpID())
                        .employeeName(request.getEmployee().getEmpFirstName()
                                + " " + request.getEmployee().getEmpLastName())
                        .employeeEmail(request.getEmployee().getEmpEmail())
                        .leaveID(request.getLeaveSheet().getEmpLeaveId())
                        .noOfDays(request.getNoOfDays())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .leaveName(request.getLeaveSheet().getLeaveType().getLeaveName())
                        .status(request.getStatus())
                        .reason(request.getReason())
                        .requestedToID(request.getRequestedTo().getEmpID())
                        .requestedToName(request.getRequestedTo().getEmpFirstName()
                                + " " + request.getRequestedTo().getEmpLastName())
                        .requestedAt(request.getRequestedAt())
                        .updatedAt(request.getUpdatedAt())
                        .build())
                .toList();

        return Map.of("message", "success", "data", response);
    }


    @Transactional
    public Map<String, Object> approveLeave(Long requestId, Long hrId, Long orgId) {

        EmployeeEntity hr = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(hrId, orgId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeaveRequestEntity request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave Request not found"));

        if (!Objects.equals(request.getOrganisation().getOrgID(), orgId)) {
            throw new RuntimeException("You don't have access to this request");
        }

        boolean isSuperAdmin = hr.getEmpRole() == EmployeeRole.SUPER_ADMIN;
        boolean isAncestor = hierarchyService.isManagerOf(
                hr.getEmpID(), request.getEmployee().getEmpID(), orgId);

        if (!isSuperAdmin && !isAncestor) {
            throw new RuntimeException("You don't have access to this request");
        }

        if (request.getStatus() != LeaveRequestStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted status leaves can be approved");
        }

        request.setStatus(LeaveRequestStatus.APPROVED);
        leaveRequestRepository.save(request);

        LeaveSheetEntity leaveSheet = leaveSheetRepository
                .findByLeaveType_LeaveCodeAndOrganisation_OrgIDAndEmployee_EmpID(
                        request.getLeaveSheet().getLeaveType().getLeaveCode(),
                        orgId,
                        request.getEmployee().getEmpID())
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leaveSheet.setUsedDays(leaveSheet.getUsedDays() + request.getNoOfDays());
        leaveSheet.setRemainingDays(leaveSheet.getRemainingDays() - request.getNoOfDays());
        leaveSheetRepository.save(leaveSheet);

        EmployeeEntity employee = request.getEmployee();
        employee.setActiveLeaveRequest(false);
        employeeRepository.save(employee);

        return Map.of("message", "Leave approved");
    }


    @Transactional
    public Map<String, Object> rejectLeave(Long requestId, Long hrId, Long orgId, String reason) {

        EmployeeEntity hr = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(hrId, orgId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeaveRequestEntity request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave Request not found"));

        if (!Objects.equals(request.getOrganisation().getOrgID(), orgId)) {
            throw new RuntimeException("You don't have access to this request");
        }

        boolean isSuperAdmin = hr.getEmpRole() == EmployeeRole.SUPER_ADMIN;
        boolean isAncestor = hierarchyService.isManagerOf(
                hr.getEmpID(), request.getEmployee().getEmpID(), orgId);

        if (!isSuperAdmin && !isAncestor) {
            throw new RuntimeException("You don't have access to this request");
        }

        if (request.getStatus() != LeaveRequestStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted status leaves can be rejected");
        }

        request.setStatus(LeaveRequestStatus.REJECTED);
        leaveRequestRepository.save(request);

        // No days deducted on rejection — just release the active flag
        EmployeeEntity employee = request.getEmployee();
        employee.setActiveLeaveRequest(false);
        employeeRepository.save(employee);

        return Map.of("message", "Leave rejected", "reason", reason);
    }
}