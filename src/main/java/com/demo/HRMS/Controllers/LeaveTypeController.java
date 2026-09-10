package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.LeaveType.CreateLeaveTypeDTO;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.LeaveTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leaveType")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;
    private final JwtService jwtService;

    public LeaveTypeController(LeaveTypeService leaveTypeService, JwtService jwtService) {
        this.leaveTypeService = leaveTypeService;
        this.jwtService = jwtService;
    }

    @PreAuthorize("hasAnyRole('HR','SUPER_ADMIN')")
    @PostMapping("/createLeaveType")
    public ResponseEntity<?> createLeaveType(@RequestBody @Valid CreateLeaveTypeDTO request) {
        Map<String, Object> response = leaveTypeService.createLeaveType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @GetMapping("/getAllLeaveTypes")
    public ResponseEntity<?> getAllLeaveTypes(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgID = jwtService.extractOrg(token);

        Map<String, Object> response = leaveTypeService.getAllLeaveTypes(orgID);
        return ResponseEntity.ok(response);
    }
}