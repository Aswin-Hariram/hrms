package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.DesignationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DesignationRepository extends JpaRepository<DesignationEntity, Long> {

    boolean existsByDesignationNameAndOrganisation_OrgID(
            String designationName,
            Long orgId
    );
}