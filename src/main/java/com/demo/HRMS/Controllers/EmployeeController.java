package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Employee.CreateEmployeeRequestDTO;
import com.demo.HRMS.DTO.Employee.EmployeeLoginRequest;
import com.demo.HRMS.DTO.Employee.EmployeeResetPassword;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Services.EmployeeService;
import com.demo.HRMS.Services.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/employee")
public class EmployeeController {



    @Autowired
    private EmployeeService empService;


    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    @PostMapping("/createEmployee")
    public ResponseEntity<?> createEmployee(
            @RequestBody @Valid CreateEmployeeRequestDTO request) {

        Map<String,Object> response = empService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody @Valid EmployeeResetPassword request) {

        empService.reset(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "Message",
                        "Password updated successfully"
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody EmployeeLoginRequest request) {

        Map<String, Object> response = empService.login(request);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    @GetMapping("/getEmployeeRole")
    public ResponseEntity<?> getEmployeeRole(@RequestParam Long empID,@RequestParam Long orgID){


        Map<String,Object> response = empService.getEmployeeRole(empID,orgID);

        return ResponseEntity.ok(response);
    }


}
