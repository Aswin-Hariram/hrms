package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.LeaveSheetEntity;
import com.demo.HRMS.Types.LeaveTypesCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveSheetRepository extends JpaRepository<LeaveSheetEntity,Long> {


    List<LeaveSheetEntity> findAllByOrganisation_OrgIDAndEmployee_EmpID(
            Long organisationOrgID,
            Long employeeEmpID
    );

    List<LeaveSheetEntity> findAllByOrganisation_OrgID(Long organisationOrgID);

    Optional<LeaveSheetEntity> findByLeaveType_LeaveCodeAndOrganisation_OrgIDAndEmployee_EmpID(
            LeaveTypesCodes leaveTypeLeaveCode,
            Long organisationOrgID,
            Long employeeEmpID);
}
