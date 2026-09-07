package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.Employee.EmployeeLoginRequest;
import com.demo.HRMS.DTO.Employee.EmployeeResetPassword;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Map;

@Service

public class EmployeeService {


    @Autowired
    private EmployeeRepository emp_repo;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


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

}
