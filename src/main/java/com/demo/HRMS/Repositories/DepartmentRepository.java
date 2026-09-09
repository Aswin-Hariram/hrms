package com.demo.HRMS.Repositories;


import com.demo.HRMS.Entities.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity,Long> {

    boolean existsByDepartmentName(String departmentName);

    Optional<DepartmentEntity> findAllByDepartmentId(Long departmentId);

    boolean existsByDepartmentNameIgnoreCaseAndOrganisation_OrgID(String departmentName, Long organisationOrgID);

}
