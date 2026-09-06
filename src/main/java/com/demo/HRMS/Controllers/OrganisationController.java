package com.demo.HRMS.Controllers;


import com.demo.HRMS.Entities.DepartmentEntity;
import com.demo.HRMS.Entities.DesignationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.DepartmentRepository;
import com.demo.HRMS.Services.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/organisation")
public class OrganisationController {

    @Autowired
    private OrganisationService orgService;



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


        request.setOrgStatus("Not Activated");
        return ResponseEntity.ok(orgService.register(request));



    }

    @PostMapping("/addDepartment")
    public ResponseEntity<?> addDepartment(@RequestBody @Valid DepartmentEntity request){

        return orgService.creatDepartment(request);

    }
    @PostMapping("/addDesignation")
    public ResponseEntity<?> addDesignation(@RequestBody @Valid DesignationEntity request){

       return orgService.creatDesignation(request);
    }

    @PostMapping("/createEmployee")
    public ResponseEntity<?> createEmployee(@RequestBody @Valid EmployeeEntity request){


        return orgService.createEmployee(request);

    }


}
