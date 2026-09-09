package com.demo.HRMS.Controllers;


import com.demo.HRMS.DTO.LeaveType.CreateLeaveTypeDTO;
import com.demo.HRMS.Entities.LeaveTypeEntity;
import com.demo.HRMS.Services.LeaveTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leaveType")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService){
        this.leaveTypeService  = leaveTypeService;
    }

    @PostMapping("/createLeaveType")
    public ResponseEntity<?> createLeaveType(@RequestBody @Valid CreateLeaveTypeDTO request){
        Map<String,Object> response = leaveTypeService.createLeaveType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR')")
    @GetMapping("/getAllLeaveTypes")
    public ResponseEntity<?> getAllLeaveTypes(@RequestParam Long orgID){

        Map<String,Object> response = leaveTypeService.getAllLeaveTypes(orgID);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
