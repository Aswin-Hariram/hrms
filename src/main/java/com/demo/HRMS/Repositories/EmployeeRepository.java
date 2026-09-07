package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity,Long> {

    Optional<EmployeeEntity> findByEmpEmailAndOrganisation_OrgID(
            String empEmail,
            Long orgID
    );
    boolean existsByEmpEmail(String emp_email);
    boolean existsByEmpEmailAndOrganisation_OrgID(String email,Long orgID);
    Optional<EmployeeEntity> findByEmpEmail(String emp_email);
}
