package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.LeaveRequestEntity;
import com.demo.HRMS.Types.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query("""
    SELECT COALESCE(SUM(l.noOfDays), 0)
    FROM LeaveRequestEntity l
    WHERE l.organisation.orgID = :orgId
      AND l.employee.empID = :employeeId
      AND l.status = :status
      AND l.startDate >= :start
      AND l.endDate <= :end
      AND l.leaveSheet.leaveType.isPaid = :isPaid
""")
    int getTotalLeaveDays(
            @Param("orgId") Long orgId,
            @Param("employeeId") Long employeeId,
            @Param("status") LeaveRequestStatus status,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("isPaid") boolean isPaid
    );

    @Query("""
    SELECT l
    FROM LeaveRequestEntity l
    WHERE l.organisation.orgID = :orgId
      AND l.employee.empID = :employeeId
      AND l.startDate <= :end
      AND l.endDate >= :start
    ORDER BY l.startDate DESC
""")
    List<LeaveRequestEntity> findLeavesForEmployee(
            @Param("orgId") Long orgId,
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
    SELECT l
    FROM LeaveRequestEntity l
    WHERE l.organisation.orgID = :orgId
      AND l.employee.empID = :employeeId
      AND l.startDate <= :end
      AND l.endDate >= :start
      AND l.status IN :statuses
    ORDER BY l.startDate DESC
""")
    List<LeaveRequestEntity> findOverlappingLeaves(
            @Param("orgId") Long orgId,
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("statuses") Collection<LeaveRequestStatus> statuses
    );
}