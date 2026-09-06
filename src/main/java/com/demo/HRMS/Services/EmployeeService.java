package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.Employee.EmployeeLoginRequest;
import com.demo.HRMS.DTO.Employee.EmployeeResetPassword;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Security.JwtService;
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


    public ResponseEntity<Map<String,String>> reset(EmployeeResetPassword request){

        if(!emp_repo.existsByEmpEmail(request.getEmpEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "Message","Email not found"
                    ));
        }


        EmployeeEntity emp = emp_repo.findByEmpEmail(request.getEmpEmail()
        ).orElseThrow(()->
                new RuntimeException("Invalid email or password")
        );
        if(!passwordEncoder.matches(request.getEmpPassword(),emp.getEmpPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "Message","Incorrect Password"
                    ));
        }
        emp.setEmpPassword(passwordEncoder.encode(request.getEmpNewPassword()));
        emp.setDefaultPasswordUpdated(true);
        emp_repo.save(emp);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "Message","Password updated successfully"
                ));
    }

    public ResponseEntity<?> login(EmployeeLoginRequest request){

        EmployeeEntity employee = emp_repo
                .findByEmpEmail(request.getEmpEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );
        if (!passwordEncoder.matches(
                request.getEmpPassword(),
                employee.getEmpPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "Status", "Login Failed",
                            "Message", "Invalid email or password"
                    )
            );
        }

        if (employee.getEmpStatus().equals("INACTIVE")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "Status", "Login Failed",
                            "Message", "Request your HR to update your Status"
                    )
            );
        }
        if (!employee.isDefaultPasswordUpdated()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "Status", "Login Failed",
                            "Message", "Reset Default Password before login"
                    )
            );
        }
        String token = jwtService.genToken(employee);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "token", token,
                        "empId", employee.getEmpID()
                )
        );
    }

}
