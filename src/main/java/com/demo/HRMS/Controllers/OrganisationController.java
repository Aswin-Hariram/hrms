package com.demo.HRMS.Controllers;


import com.demo.HRMS.DTO.Department.CreateDepartmentRequest;
import com.demo.HRMS.DTO.Designation.CreateDesignationRequest;
import com.demo.HRMS.DTO.Employee.CreateEmployeeRequestDTO;
import com.demo.HRMS.DTO.Employee.EmployeeResetPassword;
import com.demo.HRMS.Entities.DepartmentEntity;
import com.demo.HRMS.Entities.DesignationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.DepartmentRepository;
import com.demo.HRMS.Security.AuthenticatedUser;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.DepartmentService;
import com.demo.HRMS.Services.DesignationService;
import com.demo.HRMS.Services.EmployeeService;
import com.demo.HRMS.Services.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/organisation")
public class OrganisationController {


    private final DesignationService designationService;
    private final EmployeeService employeeService;
    private final OrganisationService organisationService;
    private final JwtService jwtService;
    private final DepartmentService departmentService;

    public OrganisationController(
            DesignationService designationService,
            EmployeeService employeeService,
            OrganisationService organisationService,
            JwtService jwtService,
            DepartmentService departmentService) {

        this.designationService = designationService;
        this.employeeService = employeeService;
        this.organisationService = organisationService;
        this.jwtService = jwtService;
        this.departmentService = departmentService;
    }


    @GetMapping("/check")
    public Map<String,String> health() {
        return Map.of(
                "Status","Active",
                "Message","Hello from HRMS"
        );
    }

    //Register Org
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid OrganisationEntity request) {


        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.register(request));
    }


    @PostMapping("/createAccountant")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createAccountant(@RequestParam Long empId) {

        return organisationService.createAccountant(empId);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/createDesignation")
    public ResponseEntity<?> createDesignation(@RequestBody @Valid CreateDesignationRequest request, Authentication authentication){

        String token = Objects.requireNonNull(authentication.getCredentials()).toString();
        Long orgId = jwtService.extractOrg(token);
        Map<String,Object> response = designationService.createDesignation(request,orgId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/createDepartment")
    public ResponseEntity<?> createDepartment(@RequestBody @Valid CreateDepartmentRequest request, Authentication authentication){

        String token = Objects.requireNonNull(authentication.getCredentials()).toString();
        Long orgId = jwtService.extractOrg(token);
        Map<String,Object> response = departmentService.createDepartment(request,orgId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


        @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
        @PostMapping("/createEmployee")
        public ResponseEntity<?> createEmployee(
                @RequestBody @Valid CreateEmployeeRequestDTO request,
                Authentication authentication
        ) {
            if(authentication==null) throw new RuntimeException("UnAuthorized");
            AuthenticatedUser user =
                    (AuthenticatedUser) authentication.getPrincipal();

            assert user!=null;
            Long orgId = user.organisationId();
            Map<String, Object> response = employeeService.createEmployee(request,orgId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }




    





}
