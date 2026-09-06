package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity,Long> {

    boolean existsByEmpEmail(String emp_email);
    Optional<EmployeeEntity> findByEmpEmail(String emp_email);
}
