package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.EmployeesLeaveData.CreateLeaveSheetDTO;
import com.demo.HRMS.Security.AuthenticatedUser;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.LeaveSheetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leaveSheet")
public class LeaveSheetController {

    @Autowired
    private LeaveSheetService leaveSheetService;

    @Autowired
    private JwtService jwtService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR')")
    @PostMapping("/assignLeaves")
    public ResponseEntity<?> createLeave(@RequestBody @Valid CreateLeaveSheetDTO request,Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser)  authentication.getPrincipal();
        assert user != null;
        Map<String, Object> response = leaveSheetService.assignLeave(request,user.employeeId(),user.organisationId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR')")
    @GetMapping("/leaves")
    public ResponseEntity<?> getLeaves(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        Map<String, Object> response = leaveSheetService.getLeaves(orgId);
        return ResponseEntity.ok(response);
    }
}