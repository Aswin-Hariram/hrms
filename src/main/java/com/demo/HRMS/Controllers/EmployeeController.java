package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Employee.*;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.EmployeeCompensationService;
import com.demo.HRMS.Services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService empService;

    @Autowired
    private EmployeeCompensationService employeeCompensationService;

    @Autowired
    private JwtService service;

    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    @PostMapping("/createEmployee")
    public ResponseEntity<?> createEmployee(
            @RequestBody @Valid CreateEmployeeRequestDTO request
    ) {
        Map<String, Object> response = empService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody @Valid EmployeeResetPassword request
    ) {
        empService.reset(request);
        return ResponseEntity.ok(
                Map.of("Message", "Password updated successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody EmployeeLoginRequest request
    ) {
        Map<String, Object> response = empService.login(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getEmployeeRole")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','SUPER_ADMIN')")
    public ResponseEntity<?> getEmployeeRole(
            @RequestParam Long empID,
            Authentication authentication
    ) {
        String token = (String) authentication.getCredentials();
        Long loggedEmpId = service.extractID(token);
        Long orgId = service.extractOrg(token);

        Map<String, Object> response =
                empService.getEmployeeRole(empID, loggedEmpId, orgId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','SUPER_ADMIN')")
    public ResponseEntity<?> getEmployeeProfile(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long empID = service.extractID(token);
        Long orgID = service.extractOrg(token);

        Map<String, Object> response = empService.getEmployeeProfile(empID, orgID);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/requestLeave")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','SUPER_ADMIN')")
    public ResponseEntity<?> leaveReq(
            @RequestBody @Valid EmployeeLeaveReqDTO reqDTO,
            Authentication authentication
    ) {
        String token = (String) authentication.getCredentials();
        Long empID = service.extractID(token);
        Long orgID = service.extractOrg(token);

        Map<String, Object> res = empService.leaveReq(reqDTO, empID, orgID);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/allLeaveRequest")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','SUPER_ADMIN')")
    public ResponseEntity<?> getAllRequest(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long empID = service.extractID(token);
        Long orgID = service.extractOrg(token);

        Map<String, Object> response = empService.getAllRequest(empID, orgID);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('HR','SUPER_ADMIN')")
    @PostMapping("/{empID}/compensation")
    public ResponseEntity<?> addOrUpdateCompensation(
            @PathVariable Long empID,
            @Valid @RequestBody EmployeeCompensationDTO dto,
            Authentication authentication
    ) {
        String token = (String) authentication.getCredentials();
        Long orgId = service.extractOrg(token);

        Map<String, Object> response =
                employeeCompensationService.addOrUpdateCompensation(
                        empID,
                        orgId,
                        dto
                );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('HR','SUPER_ADMIN','EMPLOYEE')")
    @GetMapping("/compensation")
    public ResponseEntity<?> addCompensation(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long empID = service.extractID(token);
        Long orgID = service.extractOrg(token);

        Map<String, Object> response =
                employeeCompensationService.getActiveCompensation(empID, orgID);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('HR','SUPER_ADMIN')")
    @GetMapping("/reportees/compensation")
    public ResponseEntity<?> getReporteeCompensations(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long hrEmpId = service.extractID(token);
        Long orgId = service.extractOrg(token);

        Map<String, Object> response =
                employeeCompensationService.getReporteeCompensations(hrEmpId, orgId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('HR','SUPER_ADMIN','EMPLOYEE')")
    @GetMapping("/myCompensation/history")
    public ResponseEntity<?> getMyCompensationHistory(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long empID = service.extractID(token);
        Long orgID = service.extractOrg(token);

        Map<String, Object> response =
                employeeCompensationService.getCompensationHistory(empID, orgID);
        return ResponseEntity.ok(response);
    }
}