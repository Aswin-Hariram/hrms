package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Employee.EmployeeLoginRequest;
import com.demo.HRMS.DTO.Employee.EmployeeResetPassword;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/employee")
public class EmployeeController {



    @Autowired
    private EmployeeService empService;

    @PostMapping("/reset")
    public ResponseEntity<?> restPassword(@RequestBody @Valid EmployeeResetPassword request){
        return empService.reset(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody EmployeeLoginRequest request) {

        return empService.login(request);

    }


}
