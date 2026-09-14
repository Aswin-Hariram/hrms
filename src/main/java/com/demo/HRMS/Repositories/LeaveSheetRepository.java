package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.LeaveSheetEntity;
import com.demo.HRMS.Types.LeaveTypesCodes;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeaveSheetRepository extends JpaRepository<LeaveSheetEntity,Long> {


    List<LeaveSheetEntity> findAllByOrganisation_OrgIDAndEmployee_EmpID(
            Long organisationOrgID,
            Long employeeEmpID
    );

    List<LeaveSheetEntity> findAllByOrganisation_OrgID(Long organisationOrgID);





    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LeaveSheetEntity> findByLeaveType_LeaveCodeAndOrganisation_OrgIDAndEmployee_EmpID(
            String leaveCode,
            Long orgID,
            Long empID);

    boolean existsByEmployee_EmpIDAndOrganisation_OrgIDAndLeaveType_LeaveId(Long empId, Long orgId, Long leaveId);
}
