package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.DesignationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DesignationRepository extends JpaRepository<DesignationEntity, Long> {

    boolean existsByDesignationNameIgnoreCaseAndOrganisation_OrgIDAndDepartment_DepartmentId(
            String name, Long orgId, Long departmentId
    );
}