package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByEmpEmailAndOrganisation_OrgID(
            String empEmail,
            Long orgID
    );

    boolean existsByEmpEmailAndOrganisation_OrgID(
            String empEmail,
            Long orgID
    );

    List<EmployeeEntity> findAllByReportToHr_EmpIDAndOrganisation_OrgID(
            Long hrEmpId,
            Long orgId
    );

    Optional<EmployeeEntity> findByEmpIDAndOrganisation_OrgID(
            Long empID,
            Long organisationOrgID
    );
}