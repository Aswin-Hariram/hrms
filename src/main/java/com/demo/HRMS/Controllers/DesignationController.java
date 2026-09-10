package com.demo.HRMS.Controllers;


import com.demo.HRMS.DTO.Designation.CreateDesignationRequest;
import com.demo.HRMS.Services.DesignationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/designation")
public class DesignationController {


    private final DesignationService designation_service;

    public DesignationController(DesignationService service){
        this.designation_service = service;
    }



    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/createDesignation")
    public ResponseEntity<?> createDesignation(@RequestBody @Valid CreateDesignationRequest request){

        Map<String,Object> response = designation_service.createDesignation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
