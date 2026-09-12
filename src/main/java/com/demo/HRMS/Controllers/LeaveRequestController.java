package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Employee.EmployeeLeaveReqDTO;
import com.demo.HRMS.DTO.LeaveRequest.LeaveHistoryRequest;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leaveRequest")
public class LeaveRequestController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LeaveRequestService leaveRequestService;


    @PostMapping("/requestLeave")
    @PreAuthorize("hasAuthority('BASIC')")
    public ResponseEntity<?> leaveReq(
            @RequestBody @Valid EmployeeLeaveReqDTO reqDTO,
            Authentication authentication
    ) {
        String token = (String) authentication.getCredentials();
        Long empID = jwtService.extractID(token);
        Long orgID = jwtService.extractOrg(token);

        Map<String, Object> res = leaveRequestService.leaveReq(reqDTO, empID, orgID);
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR')")
    @GetMapping("/getRequestedLeaves")
    public ResponseEntity<?> getRequestedLeaves(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long empID = jwtService.extractID(token);
        Long orgID = jwtService.extractOrg(token);

        Map<String, Object> response =
                leaveRequestService.getAllLeaveRequests(empID, orgID);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('HR','SUPER_ADMIN')")
    @PostMapping("/approve")
    public ResponseEntity<?> approveRequestedLeave(
            @RequestParam Long reqId, Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long hrId = jwtService.extractID(token);
        Long orgID = jwtService.extractOrg(token);

        Map<String, Object> response =
                leaveRequestService.approveLeave(reqId, hrId, orgID);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('HR','SUPER_ADMIN')")
    @PostMapping("/reject")
    public ResponseEntity<?> rejectRequestedLeave(
            @RequestParam Long reqId,
            @RequestParam String reason,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long hrId = jwtService.extractID(token);
        Long orgID = jwtService.extractOrg(token);

        Map<String, Object> response =
                leaveRequestService.rejectLeave(reqId, hrId, orgID, reason);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','SUPER_ADMIN')")
    @PostMapping("/getLeaveHistory")
    public ResponseEntity<?> getLeaveHistory(
            @Valid @RequestBody LeaveHistoryRequest request,

            Authentication authentication) {


        String token = (String) authentication.getCredentials();
        Long hrId = jwtService.extractID(token);
        Long orgID = jwtService.extractOrg(token);

        Map<String, Object> response =
                leaveRequestService.getLeaveHistory(request.getEmpId(), hrId, orgID,request.getFromDate(),request.getEndDate());
        return ResponseEntity.ok(response);
    }
}