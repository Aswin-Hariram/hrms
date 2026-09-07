package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Employee.EmployeeLoginRequest;
import com.demo.HRMS.DTO.Employee.EmployeeResetPassword;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/employee")
public class EmployeeController {



    @Autowired
    private EmployeeService empService;

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
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody EmployeeLoginRequest request) {

        Map<String, Object> response = empService.login(request);

        return ResponseEntity.ok(response);
    }


}
