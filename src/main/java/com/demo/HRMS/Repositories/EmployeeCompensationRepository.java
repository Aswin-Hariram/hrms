package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeCompensationRepository
        extends JpaRepository<EmployeeCompensationEntity, Long> {

    Optional<EmployeeCompensationEntity> findByEmployee_EmpIDAndActiveTrue(Long empID);

    List<EmployeeCompensationEntity> findAllByEmployee_EmpIDInAndActiveTrue(List<Long> empIds);

    boolean existsByEmployee_EmpIDAndActiveTrue(Long empID);

    @Query("""
            SELECT c FROM EmployeeCompensationEntity c
            WHERE c.employee.empID = :empID
            ORDER BY c.effectiveFrom DESC
            """)
    List<EmployeeCompensationEntity> findHistoryByEmployeeId(@Param("empID") Long empID);
}