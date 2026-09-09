package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.Employee.CreateEmployeeRequestDTO;
import com.demo.HRMS.DTO.Employee.EmployeeLoginRequest;
import com.demo.HRMS.DTO.Employee.EmployeeResetPassword;
import com.demo.HRMS.EmployeeRole;
import com.demo.HRMS.Entities.DepartmentEntity;
import com.demo.HRMS.Entities.DesignationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.DepartmentRepository;
import com.demo.HRMS.Repositories.DesignationRepository;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import com.demo.HRMS.Security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

@Service

public class EmployeeService {


    @Autowired
    private EmployeeRepository emp_repo;

    private EmployeeRole employeeRole;

    @Autowired
    private OrganisationRepository org_repo;
    @Autowired
    private DepartmentRepository dep_repo;
    @Autowired
    private DesignationRepository desg_repo;



    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    private JwtService jwtService;



    @Transactional
    public void reset(EmployeeResetPassword request) {

        EmployeeEntity emp = emp_repo
                .findByEmpEmailAndOrganisation_OrgID(
                        request.getEmpEmail(),
                        request.getOrgID()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or organisation ID"
                        )
                );

        if (!passwordEncoder.matches(
                request.getEmpPassword(),
                emp.getEmpPassword()
        )) {
            throw new RuntimeException("Incorrect password");
        }

        emp.setEmpPassword(
                passwordEncoder.encode(request.getEmpNewPassword())
        );

        emp.setDefaultPasswordUpdated(true);
        emp.setEmpStatus("Active");

        emp_repo.save(emp);
    }

    public Map<String, Object> login(EmployeeLoginRequest request) {

        EmployeeEntity employee = emp_repo.findByEmpEmailAndOrganisation_OrgID(
                        request.getEmpEmail(),
                        request.getOrgID()
                ).orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        if (!passwordEncoder.matches(request.getEmpPassword(), employee.getEmpPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        if ("INACTIVE".equalsIgnoreCase(employee.getEmpStatus())) {
            throw new RuntimeException(
                    "Request your HR to update your status"
            );
        }

        if (!employee.isDefaultPasswordUpdated()) {
            throw new RuntimeException(
                    "Reset default password before login"
            );
        }

        String token = jwtService.genToken(employee);

        return Map.of(
                "message", "Login successful",
                "token", token,
                "empId", employee.getEmpID()
        );
    }


    //Create Employee
    @Transactional
    public Map<String,Object> createEmployee(CreateEmployeeRequestDTO request){

        if (emp_repo.existsByEmpEmail(request.getEmpEmail())) {
            throw new DataIntegrityViolationException(
                    "Employee already exists with same email address"

            );
        };
        if (!Arrays.asList(EmployeeRole.values()).contains(request.getEmpRole())) {
            throw new RuntimeException("Invalid role");
        }
        OrganisationEntity organisation = org_repo.getReferenceById(request.getOrgID());
        DesignationEntity designation = null;
        EmployeeEntity report_to_hr = null;

        if (request.getDesignationId() != null) {
            designation = desg_repo.getReferenceById(request.getDesignationId());
        }
        DepartmentEntity department = null;

        if (request.getDepartmentId() != null) {
            department = dep_repo.getReferenceById(request.getDepartmentId());
        }

        if(request.getReportToHr()!=null){
            report_to_hr = emp_repo.findById(request.getReportToHr()).orElseThrow(()->
                        new RuntimeException("Report to HR not found.")
                    );
        }


        EmployeeEntity employee = EmployeeEntity.builder()
                .organisation(organisation)
                .empFirstName(request.getEmpFirstName())
                .empLastName(request.getEmpLastName())
                .empEmail(request.getEmpEmail())
                .empPhoneNumber(request.getEmpPhoneNumber())
                .empPassword(passwordEncoder.encode(request.getEmpFirstName() + request.getEmpPhoneNumber()))
                .empDOB(request.getEmpDOB())
                .age(request.getAge())
                .empJoiningDate(request.getEmpJoiningDate())
                .empType(request.getEmpType())
                .empStatus(request.getEmpStatus())
                .designation(designation)
                .department(department)
                .empRole(request.getEmpRole())
                .defaultPasswordUpdated(false)
                .reportToHr(report_to_hr)
                .build();

        emp_repo.save(employee);



        return Map.of(
                "message","Success",
                "email",employee.getEmpEmail(),
                "default_pass",employee.getEmpFirstName() + employee.getEmpPhoneNumber(),
                "Report_to",employee.getReportToHr().getEmpFirstName()
        );

    }
    public Map<String,Object> getEmployeeRole(Long empId,Long orgId){

        EmployeeEntity employee = emp_repo.findById(empId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));
        if (!employee.getOrganisation().getOrgID().equals(orgId)) {
            throw new RuntimeException(
                    "Employee does not belong to this organisation"
            );
        }


        return Map.of(
                "empID",employee.getEmpID(),
                "role",employee.getEmpRole()
        );
    }

}
