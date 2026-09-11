package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.LeaveRequestEntity;
import com.demo.HRMS.Types.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, Long> {

    List<LeaveRequestEntity> findAllByOrganisation_OrgIDAndEmployee_EmpID(
            Long organisationOrgID, Long employeeEmpID);

    List<LeaveRequestEntity> findAllByOrganisation_OrgIDAndRequestedToAndStatus(
            Long organisationOrgID, EmployeeEntity requestedTo, LeaveRequestStatus status);


    List<LeaveRequestEntity> findAllByOrganisation_OrgIDAndEmployee_EmpIDInAndStatus(
            Long organisationOrgID,
            Collection<Long> employeeEmpIDs,
            LeaveRequestStatus status);
}