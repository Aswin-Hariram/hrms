package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.PayslipEntity;
import com.demo.HRMS.Types.PayslipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<PayslipEntity, Long> {

    Optional<PayslipEntity> findByEmployee_EmpIDAndOrganisation_OrgIDAndPeriodStartAndPeriodEnd(
            Long empID, Long orgId, LocalDate periodStart, LocalDate periodEnd);

    List<PayslipEntity> findByEmployee_EmpIDAndOrganisation_OrgIDOrderByPeriodEndDesc(
            Long empID, Long orgId);

    List<PayslipEntity> findByOrganisation_OrgIDAndDepartment_DepartmentIdAndPeriodEnd(
            Long orgId, Long departmentId, LocalDate periodEnd);

    Optional<PayslipEntity> findByPayslipIdAndOrganisation_OrgID(Long payslipId, Long orgId);

    List<PayslipEntity> findByOrganisation_OrgIDAndStatus(
            Long orgId, PayslipStatus status);

    List<PayslipEntity> findByOrganisation_OrgIDAndDepartment_DepartmentIdAndStatus(
            Long orgId, Long departmentId, PayslipStatus status);

    List<PayslipEntity> findByOrganisation_OrgIDAndStatusAndPeriodEndBetween(
            Long orgId, PayslipStatus status,
            LocalDate from, LocalDate to);

    List<PayslipEntity> findByOrganisation_OrgID(Long organisationOrgID);
}