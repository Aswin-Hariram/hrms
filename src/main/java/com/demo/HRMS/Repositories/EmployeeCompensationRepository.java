package com.demo.HRMS.Repositories;
import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeCompensationRepository
        extends JpaRepository<EmployeeCompensationEntity, Long> {

    Optional<EmployeeCompensationEntity> findByEmployee_EmpID(Long empID);
}