package com.demo.HRMS.Controllers;


import com.demo.HRMS.DTO.Department.CreateDepartmentRequest;
import com.demo.HRMS.Services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    private final DepartmentService dep_service;

    public DepartmentController(DepartmentService dep_service){
        this.dep_service = dep_service;
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/createDepartment")
    public ResponseEntity<?> createDepartment(@RequestBody @Valid CreateDepartmentRequest request){

        Map<String,Object> response = dep_service.createDepartment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
