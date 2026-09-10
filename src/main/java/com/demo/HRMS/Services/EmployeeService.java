package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Employee.CreateEmployeeRequestDTO;
import com.demo.HRMS.DTO.Employee.EmployeeLeaveReqDTO;
import com.demo.HRMS.DTO.Employee.EmployeeLoginRequest;
import com.demo.HRMS.DTO.Employee.EmployeeResetPassword;
import com.demo.HRMS.DTO.Employee.Response.GetEmployeeProfileDTO;
import com.demo.HRMS.DTO.Employee.Response.LeaveRequestResponseDTO;
import com.demo.HRMS.DTO.LeaveSheet.Response.LeaveSheetResponseDTO;
import com.demo.HRMS.Entities.*;
import com.demo.HRMS.Repositories.*;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Types.EmployeeRole;
import com.demo.HRMS.Types.EmploymentType;
import com.demo.HRMS.Types.LeaveRequestStatus;
import com.demo.HRMS.Types.LeaveTypesCodes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EmployeeService {

    private static final String STATUS_ACTIVE = "ACTIVE";

    @Autowired
    private EmployeeRepository emp_repo;

    @Autowired
    private OrganisationRepository org_repo;

    @Autowired
    private DepartmentRepository dep_repo;

    @Autowired
    private DesignationRepository desg_repo;

    @Autowired
    private LeaveSheetRepository leaveSheetRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Transactional
    public void reset(EmployeeResetPassword request) {

        EmployeeEntity emp = emp_repo
                .findByEmpEmailAndOrganisation_OrgID(
                        request.getEmpEmail(),
                        request.getOrgID()
                )
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or organisation ID")
                );

        if (!passwordEncoder.matches(
                request.getEmpPassword(),
                emp.getEmpPassword()
        )) {
            throw new RuntimeException("Incorrect password");
        }

        emp.setEmpPassword(passwordEncoder.encode(request.getEmpNewPassword()));
        emp.setDefaultPasswordUpdated(true);
        emp.setEmpStatus(STATUS_ACTIVE);

        emp_repo.save(emp);
    }


    public Map<String, Object> login(EmployeeLoginRequest request) {

        EmployeeEntity employee = emp_repo
                .findByEmpEmailAndOrganisation_OrgID(
                        request.getEmpEmail(),
                        request.getOrgID()
                )
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getEmpPassword(),
                employee.getEmpPassword()
        )) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!employee.isDefaultPasswordUpdated()) {
            throw new RuntimeException("Reset default password before login");
        }

        if (!STATUS_ACTIVE.equalsIgnoreCase(employee.getEmpStatus())) {
            throw new RuntimeException("Request your HR to update your status");
        }

        String token = jwtService.genToken(employee);

        return Map.of(
                "message", "Login successful",
                "token", token,
                "empId", employee.getEmpID()
        );
    }

    @Transactional
    public Map<String, Object> createEmployee(CreateEmployeeRequestDTO request) {

        // Email uniqueness within org
        if (emp_repo.existsByEmpEmailAndOrganisation_OrgID(
                request.getEmpEmail(),
                request.getOrgID()
        )) {
            throw new DataIntegrityViolationException(
                    "Employee already exists with same email in this organisation"
            );
        }

        if (!Arrays.asList(EmployeeRole.values()).contains(request.getEmpRole())) {
            throw new RuntimeException("Invalid role");
        }

        OrganisationEntity organisation = org_repo.findById(request.getOrgID())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        DesignationEntity designation = null;
        if (request.getDesignationId() != null) {
            designation = desg_repo.findById(request.getDesignationId())
                    .orElseThrow(() -> new RuntimeException("Designation not found"));
            if (!Objects.equals(
                    designation.getOrganisation().getOrgID(),
                    request.getOrgID()
            )) {
                throw new RuntimeException("Designation does not belong to this organisation");
            }
        }

        DepartmentEntity department = null;
        if (request.getDepartmentId() != null) {
            department = dep_repo.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            if (!Objects.equals(
                    department.getOrganisation().getOrgID(),
                    request.getOrgID()
            )) {
                throw new RuntimeException("Department does not belong to this organisation");
            }
        }

        EmployeeEntity reportToHr = null;
        if (request.getReportToHr() != null) {
            reportToHr = emp_repo.findById(request.getReportToHr())
                    .orElseThrow(() -> new RuntimeException("Report-to HR not found"));
            if (!Objects.equals(
                    reportToHr.getOrganisation().getOrgID(),
                    request.getOrgID()
            )) {
                throw new RuntimeException("Report-to HR does not belong to this organisation");
            }
        }

        String defaultPassword =
                request.getEmpFirstName() + request.getEmpPhoneNumber();

        EmployeeEntity employee = EmployeeEntity.builder()
                .organisation(organisation)
                .empFirstName(request.getEmpFirstName())
                .empLastName(request.getEmpLastName())
                .empEmail(request.getEmpEmail())
                .empPhoneNumber(request.getEmpPhoneNumber())
                .empPassword(passwordEncoder.encode(defaultPassword))
                .empDOB(request.getEmpDOB())
                .age(request.getAge())
                .empJoiningDate(request.getEmpJoiningDate())
                .empType(EmploymentType.valueOf(request.getEmpType().toUpperCase()))
                .empStatus(STATUS_ACTIVE)
                .designation(designation)
                .department(department)
                .empRole(request.getEmpRole())
                .defaultPasswordUpdated(false)
                .reportToHr(reportToHr)
                .build();

        emp_repo.save(employee);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Success");
        response.put("email", employee.getEmpEmail());
        response.put("default_pass", defaultPassword);
        response.put(
                "Report_to",
                reportToHr != null ? reportToHr.getEmpFirstName() : null
        );
        return response;
    }

    public Map<String, Object> getEmployeeRole(Long empId, Long orgId) {
        EmployeeEntity employee = emp_repo
                .findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return Map.of(
                "empID", employee.getEmpID(),
                "role", employee.getEmpRole()
        );
    }

    @Transactional
    public Map<String, Object> getEmployeeProfile(Long empId, Long orgId) {

        EmployeeEntity employee = emp_repo
                .findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<LeaveSheetEntity> leaveSheetEntitiesList =
                leaveSheetRepository.findAllByOrganisation_OrgIDAndEmployee_EmpID(
                        orgId,
                        empId
                );

        List<LeaveSheetResponseDTO> employeeLeave = new ArrayList<>();
        for (LeaveSheetEntity leaveSheet : leaveSheetEntitiesList) {
            employeeLeave.add(
                    LeaveSheetResponseDTO.builder()
                            .leaveID(leaveSheet.getLeaveType().getLeaveId())
                            .leaveName(leaveSheet.getLeaveType().getLeaveName())
                            .allocatedDays(leaveSheet.getAllocatedDays())
                            .usedDays(leaveSheet.getUsedDays())
                            .remainingDays(leaveSheet.getRemainingDays())
                            .build()
            );
        }

        String reportingToHR = employee.getReportToHr() != null
                ? employee.getReportToHr().getEmpFirstName()
                : null;

        GetEmployeeProfileDTO employeeProfileDTO = GetEmployeeProfileDTO.builder()
                .empID(employee.getEmpID())
                .empFirstName(employee.getEmpFirstName())
                .empLastName(employee.getEmpLastName())
                .empEmail(employee.getEmpEmail())
                .empPhoneNumber(employee.getEmpPhoneNumber())
                .empDOB(employee.getEmpDOB())
                .age(employee.getAge())
                .empJoiningDate(employee.getEmpJoiningDate())
                .empType(employee.getEmpType().name())
                .empStatus(employee.getEmpStatus())
                .empRole(employee.getEmpRole())
                .ReportingToHR(reportingToHR)
                .leaveSheetEntitiesList(employeeLeave)
                .build();

        return Map.of(
                "message", "Success",
                "data", employeeProfileDTO
        );
    }


    @Transactional
    public Map<String, Object> leaveReq(EmployeeLeaveReqDTO reqDTO, Long empId, Long orgId) {

        EmployeeEntity employee = emp_repo
                .findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.isActiveLeaveRequest()) {
            throw new RuntimeException("Leave request already exists");
        }

        if (employee.getReportToHr() == null) {
            throw new RuntimeException("Employee does not have a reporting HR");
        }

        if (reqDTO.getEndDate().isBefore(reqDTO.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        String codeStr = reqDTO.getLeaveCode().toUpperCase();
        if (Arrays.stream(LeaveTypesCodes.values())
                .noneMatch(code -> code.name().equalsIgnoreCase(codeStr))) {
            throw new RuntimeException("Invalid leave code");
        }

        LeaveTypesCodes code = LeaveTypesCodes.valueOf(codeStr);

        LeaveSheetEntity leaveSheet =
                leaveSheetRepository
                        .findByLeaveType_LeaveCodeAndOrganisation_OrgIDAndEmployee_EmpID(
                                code,
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
                .allocatedDays(leaveSheet.getAllocatedDays())
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

        employee.setActiveLeaveRequest(true);
        emp_repo.save(employee);

        return Map.of(
                "message", "success",
                "data", responseDTO,
                "Days req", noOfDays,
                "Request ID", savedRequest.getRequestID()
        );
    }

    public Map<String, Object> getAllRequest(Long empId, Long orgId) {

        EmployeeEntity employee = emp_repo
                .findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<LeaveRequestEntity> allLeaveRequest =
                leaveRequestRepository
                        .findAllByOrganisation_OrgIDAndEmployee_EmpID(orgId, empId);

        List<LeaveRequestResponseDTO> response = allLeaveRequest.stream()
                .map(request -> LeaveRequestResponseDTO.builder()
                        .requestID(request.getRequestID())
                        .empID(request.getEmployee().getEmpID())
                        .employeeName(
                                request.getEmployee().getEmpFirstName()
                                        + " "
                                        + request.getEmployee().getEmpLastName()
                        )
                        .orgID(request.getOrganisation().getOrgID())
                        .leaveID(request.getRequestID())
                        .noOfDays(request.getNoOfDays())
                        .leaveName(
                                request.getLeaveSheet().getLeaveType().getLeaveName()
                        )
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .status(request.getStatus())
                        .reason(request.getReason())
                        .requestedToEmpID(request.getRequestedTo().getEmpID())
                        .requestedToName(
                                request.getRequestedTo().getEmpFirstName()
                                        + " "
                                        + request.getRequestedTo().getEmpLastName()
                        )
                        .requestedAt(request.getRequestedAt())
                        .updatedAt(request.getUpdatedAt())
                        .build()
                )
                .toList();

        return Map.of(
                "message", "success",
                "data", response
        );
    }
}