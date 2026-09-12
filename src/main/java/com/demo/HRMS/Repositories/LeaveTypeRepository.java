package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.LeaveTypeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveTypeEntity,Long> {


    boolean existsByOrganisation_OrgIDAndLeaveCodeIgnoreCase(Long orgId, String code);
    List<LeaveTypeEntity> findByOrganisation_OrgID(Long organisationOrgID);

    boolean existsByLeaveCodeIgnoreCase(String leaveCode);
    Optional<LeaveTypeEntity> findByOrganisation_OrgIDAndLeaveId(Long organisationOrgID, Long leaveId);
    boolean existsByOrganisation_OrgIDAndLeaveNameIgnoreCase(Long orgId, String leaveName);
}
