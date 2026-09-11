package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.PayslipEntity;
import com.demo.HRMS.Types.PayslipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<PayslipEntity, Long> {

    Optional<PayslipEntity> findByEmployee_EmpIDAndPayMonthAndPayYear(
            Long empId, int payMonth, int payYear);

    List<PayslipEntity> findByEmployee_EmpIDOrderByPayYearDescPayMonthDesc(Long empId);

    List<PayslipEntity> findByOrganisation_OrgIdAndPayMonthAndPayYear(
            Long orgId, int payMonth, int payYear);

    List<PayslipEntity> findByOrganisation_OrgIdAndStatus(
            Long orgId, PayslipStatus status);

    boolean existsByEmployee_EmpIDAndPayMonthAndPayYear(
            Long empId, int payMonth, int payYear);

    @Query("SELECT p FROM PayslipEntity p " +
            "WHERE p.organisation.orgID = :orgId " +
            "AND p.payYear = :year " +
            "ORDER BY p.payMonth DESC")
    List<PayslipEntity> findByOrgAndYear(
            @Param("orgId") Long orgId,
            @Param("year") int year);


    @Query("SELECT p FROM PayslipEntity p " +
            "JOIN FETCH p.employee e " +
            "LEFT JOIN FETCH e.department " +
            "LEFT JOIN FETCH e.designation " +
            "LEFT JOIN FETCH p.compensation " +
            "WHERE p.payslipId = :id")
    Optional<PayslipEntity> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM PayslipEntity p " +
            "JOIN FETCH p.employee e " +
            "LEFT JOIN FETCH e.department " +
            "LEFT JOIN FETCH e.designation " +
            "LEFT JOIN FETCH p.compensation " +
            "WHERE e.empID = :empId " +
            "ORDER BY p.payYear DESC, p.payMonth DESC")
    List<PayslipEntity> findByEmployeeWithDetails(@Param("empId") Long empId);

    @Query("SELECT p FROM PayslipEntity p " +
            "JOIN FETCH p.employee e " +
            "LEFT JOIN FETCH e.department " +
            "LEFT JOIN FETCH e.designation " +
            "LEFT JOIN FETCH p.compensation " +
            "WHERE p.organisation.orgID = :orgId " +
            "AND p.payMonth = :month AND p.payYear = :year")
    List<PayslipEntity> findByOrgAndPeriodWithDetails(
            @Param("orgId") Long orgId,
            @Param("month") int month,
            @Param("year") int year);
}