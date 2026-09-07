package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.Employee.CreateEmployeeRequestDTO;
import com.demo.HRMS.EmployeeRole;
import com.demo.HRMS.Entities.DepartmentEntity;
import com.demo.HRMS.Entities.DesignationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.DepartmentRepository;
import com.demo.HRMS.Repositories.DesignationRepository;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Map;

@Service
public class OrganisationService {


    @Autowired
    private OrganisationRepository org_repo;
    @Autowired
    private DepartmentRepository dep_repo;
    @Autowired
    private DesignationRepository desg_repo;


    @Autowired
    private EmployeeRepository emp_repo;


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();




    //Register Organisation
    @Transactional
    public Map<String,Object> register(OrganisationEntity organisation){


        if (org_repo.existsByOrgEmail(organisation.getOrgEmail())) {
            throw new DataIntegrityViolationException("Organisation with this email already exists");
        }



        org_repo.save(organisation);
        EmployeeEntity superAdmin = new EmployeeEntity();
        superAdmin.setOrganisation(organisation);

        superAdmin.setEmpFirstName("System");
        superAdmin.setEmpLastName("Admin");



        superAdmin.setEmpEmail(organisation.getOrgEmail());

        superAdmin.setEmpPassword(passwordEncoder.encode("admin@"+organisation.getOrgPhone()));
        superAdmin.setEmpPhoneNumber(organisation.getOrgPhone());
        superAdmin.setEmpDOB(LocalDate.of(2000, 1, 1));
        superAdmin.setAge(26);
        superAdmin.setEmpJoiningDate(LocalDate.now());

        superAdmin.setEmpType("ADMIN");
        superAdmin.setEmpStatus("NOT ACTIVE");
        superAdmin.setEmpRole(EmployeeRole.SUPER_ADMIN);

        emp_repo.save(superAdmin);




        return Map.of(
                "Status","Successful",
                "org_details", organisation,
                "admin_details", Map.of(
                        "email", superAdmin.getEmpEmail(),
                        "role", superAdmin.getEmpRole(),
                        "status", superAdmin.getEmpStatus(),
                        "DefaultPass", "admin@"+organisation.getOrgPhone()
                )

        );


    }



    //Create Department
    public ResponseEntity<?> creatDepartment(DepartmentEntity request){

        if(dep_repo.existsByDepartmentName(request.getDepartmentName())){
            throw new DataIntegrityViolationException("Department Already exists");
        }
        DepartmentEntity savedDepartment = dep_repo.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message","Successfull",
                        "departmentID",savedDepartment.getDepartmentId()
                )
        );

    }


    //Create Designation
    @Transactional
    public ResponseEntity<?> creatDesignation(DesignationEntity request) {

        Long orgId = request.getOrganisation().getOrgID();

        OrganisationEntity organisation = org_repo.findById(orgId)
                .orElseThrow(() ->
                        new RuntimeException("Organisation not found")
                );

        if (desg_repo.existsByDesignationNameAndOrganisation_OrgID(
                request.getDesignationName(),
                orgId
        )) {
            throw new DataIntegrityViolationException(
                    "Designation already exists in this organisation"
            );
        }

        request.setOrganisation(organisation);

        DesignationEntity savedDesignation = desg_repo.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Successful",
                        "designationID", savedDesignation.getDesignationId()
                )
        );
    }
    //Create Employee
    public Map<String,Object> createEmployee(CreateEmployeeRequestDTO request){

        if (emp_repo.existsByEmpEmail(request.getEmpEmail())) {
            throw new DataIntegrityViolationException(
                    "Employee already exists with same email address"

            );
        };
        if (!EnumSet.of(
                EmployeeRole.SUPER_ADMIN,
                EmployeeRole.HR
        ).contains(request.getEmpRole())) {
            return
                    Map.of(
                            "status","failed",
                            "message","invalid role"
                    );
        }
        OrganisationEntity organisation = org_repo.getReferenceById(request.getOrgID());
        DesignationEntity designation = null;

        if (request.getDesignationId() != null) {
            designation = desg_repo.getReferenceById(request.getDesignationId());
        }
        DepartmentEntity department = null;

        if (request.getDepartmentId() != null) {
            department = dep_repo.getReferenceById(request.getDepartmentId());
        }


        EmployeeEntity employee = EmployeeEntity.builder()
                .organisation(organisation)
                .empFirstName(request.getEmpFirstName())
                .empLastName(request.getEmpLastName())
                .empEmail(request.getEmpEmail())
                .empPhoneNumber(request.getEmpPhoneNumber())
                .empPassword(
                        request.getEmpFirstName()
                                + request.getEmpPhoneNumber()
                )
                .empDOB(request.getEmpDOB())
                .age(request.getAge())
                .empJoiningDate(request.getEmpJoiningDate())
                .empType(request.getEmpType())
                .empStatus(request.getEmpStatus())
                .designation(designation)
                .department(department)
                .empRole(request.getEmpRole())
                .defaultPasswordUpdated(false)
                .build();

        EmployeeEntity savedEmployee = emp_repo.save(employee);


        return Map.of("message","Success","data",savedEmployee);

    }

}
