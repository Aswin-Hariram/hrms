package com.demo.HRMS.Controllers;


import com.demo.HRMS.DTO.EmployeesLeaveData.CreateLeaveSheetDTO;
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
    @PostMapping("/createLeave")
    public ResponseEntity<?> createLeave(@RequestBody @Valid CreateLeaveSheetDTO request){

        Map<String,Object> response = leaveSheetService.createLeave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR')")
    @GetMapping("/leaves")
    public ResponseEntity<?> getLeaves(@RequestParam Long orgId){

        Map<String,Object> response = leaveSheetService.getLeaves(orgId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }






}
