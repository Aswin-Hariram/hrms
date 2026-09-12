package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Employee.EmployeeLeaveReqDTO;
import com.demo.HRMS.DTO.LeaveRequest.LeaveHistoryResponse;
import com.demo.HRMS.DTO.LeaveRequest.Response.PendingLeaveResponse;
import com.demo.HRMS.DTO.LeaveSheet.Response.LeaveSheetResponseDTO;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.LeaveRequestEntity;
import com.demo.HRMS.Entities.LeaveSheetEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.LeaveRequestRepository;
import com.demo.HRMS.Repositories.LeaveSheetRepository;
import com.demo.HRMS.Repositories.LeaveTypeRepository;
import com.demo.HRMS.Types.EmployeeRole;
import com.demo.HRMS.Types.LeaveRequestStatus;
import com.demo.HRMS.Types.LeaveTypesCodes;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LeaveRequestService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveSheetRepository leaveSheetRepository;
    private final HierarchyService hierarchyService;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveRequestService(EmployeeRepository employeeRepository,
                               LeaveRequestRepository leaveRequestRepository,
                               LeaveSheetRepository leaveSheetRepository,
                               HierarchyService hierarchyService, LeaveTypeRepository leaveTypeRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveSheetRepository = leaveSheetRepository;
        this.hierarchyService = hierarchyService;
        this.leaveTypeRepository = leaveTypeRepository;
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

        employeeRepository.save(employee);

        return Map.of("message", "Leave rejected", "reason", reason);
    }

    @Transactional
    public Map<String,Object> getLeaveHistory(Long empId,Long hrId, Long orgId, LocalDate fromDate, LocalDate toDate){


        if(fromDate.isAfter(toDate)){
            throw new IllegalArgumentException("Invalid dates");
        }
        EmployeeEntity emp = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmployeeEntity reqestedByEmp = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(hrId, orgId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isSelf = reqestedByEmp.getEmpRole() == EmployeeRole.EMPLOYEE && Objects.equals(reqestedByEmp.getEmpID(), emp.getEmpID());
        boolean isSuperAdmin = reqestedByEmp.getEmpRole() == EmployeeRole.SUPER_ADMIN;
        boolean isAncestor = hierarchyService.isManagerOf(
                reqestedByEmp.getEmpID(), emp.getEmpID(), orgId);

        if (!isSelf&&!isSuperAdmin && !isAncestor) {
            throw new RuntimeException("You don't have access to this request");
        }


        List<LeaveHistoryResponse> leavesTaken = leaveRequestRepository
                .findLeavesForEmployee(orgId, empId, fromDate, toDate)
                .stream()
                .map(l -> new LeaveHistoryResponse(
                        l.getRequestID(),
                        l.getLeaveSheet().getLeaveType().getLeaveName(),
                        l.getRequestID(),
                        l.getLeaveSheet().getLeaveType().getLeaveCode(),
                        l.getStartDate(),
                        l.getEndDate(),
                        l.getNoOfDays(),
                        l.getLeaveSheet().getLeaveType().isPaid(),
                        l.getStatus()
                ))
                .toList();

        return Map.of(
                "message","success",
                "data",leavesTaken
        );
    }


    @Transactional
    public Map<String, Object> leaveReq(EmployeeLeaveReqDTO reqDTO, Long empId, Long orgId) {

        if(reqDTO.getStartDate().isAfter(reqDTO.getEndDate())){
            throw new DateTimeException("Invalid Dates");
        }

        String codeStr = reqDTO.getLeaveCode().toUpperCase();


        if(!leaveTypeRepository.existsByLeaveCodeIgnoreCase(codeStr)){
            throw new RuntimeException("Invalid leave code");
        }
        EmployeeEntity employee = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!leaveRequestRepository.findOverlappingLeaves(
                orgId,
                empId,
                reqDTO.getStartDate(),
                reqDTO.getEndDate(),
                List.of(
                        LeaveRequestStatus.SUBMITTED,
                        LeaveRequestStatus.APPROVED
                )
        ).isEmpty()) {
            throw new RuntimeException("Leave request already exists");
        }

        if (employee.getReportToHr() == null) {
            throw new RuntimeException("Employee does not have a reporting HR");
        }

        LeaveSheetEntity leaveSheet =
                leaveSheetRepository
                        .findByLeaveType_LeaveCodeAndOrganisation_OrgIDAndEmployee_EmpID(
                                codeStr,
                                orgId,
                                empId
                        )
                        .orElseThrow(() -> new RuntimeException("Invalid request"));

        int noOfDays = (int) (
                ChronoUnit.DAYS.between(
                        reqDTO.getStartDate(),
                        reqDTO.getEndDate()
                ) + 1
        );

        if (noOfDays > leaveSheet.getRemainingDays()) {
            throw new RuntimeException("Insufficient available days.");
        }

        LeaveSheetResponseDTO responseDTO = LeaveSheetResponseDTO.builder()
                .leaveID(leaveSheet.getLeaveType().getLeaveId())
                .leaveName(leaveSheet.getLeaveType().getLeaveName())
                .empID(empId)
                .leaveCode(leaveSheet.getLeaveType().getLeaveCode())
                .allocatedDays(leaveSheet.getLeaveType().getNoDays())
                .usedDays(leaveSheet.getUsedDays())
                .remainingDays(leaveSheet.getRemainingDays())
                .build();

        LeaveRequestEntity request = LeaveRequestEntity.builder()
                .employee(employee)
                .organisation(employee.getOrganisation())
                .startDate(reqDTO.getStartDate())
                .endDate(reqDTO.getEndDate())
                .leaveSheet(leaveSheet)
                .reason(reqDTO.getReason())
                .status(LeaveRequestStatus.SUBMITTED)
                .noOfDays(noOfDays)
                .requestedTo(employee.getReportToHr())
                .build();

        LeaveRequestEntity savedRequest = leaveRequestRepository.save(request);


        return Map.of(
                "message", "success",
                "data", responseDTO,
                "Days req", noOfDays,
                "Request ID", savedRequest.getRequestID()
        );
    }


}