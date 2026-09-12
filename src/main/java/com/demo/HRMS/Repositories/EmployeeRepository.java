package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByEmpEmailAndOrganisation_OrgID(
            String empEmail, Long orgID);

    boolean existsByEmpEmailAndOrganisation_OrgID(
            String empEmail, Long orgID);

    List<EmployeeEntity> findAllByReportToHr_EmpIDAndOrganisation_OrgID(
            Long hrEmpId, Long orgId);

    Optional<EmployeeEntity> findByEmpIDAndOrganisation_OrgID(
            Long empID, Long organisationOrgID);

    @Query(value = """
    WITH RECURSIVE chain AS (
        SELECT 
            e.emp_id,
            e.org_id,
            e.report_to_hr,
            0 AS depth
        FROM Employee e
        WHERE e.emp_id = :empID
          AND e.org_id = :orgID

        UNION ALL

        SELECT 
            parent.emp_id,
            parent.org_id,
            parent.report_to_hr,
            c.depth + 1
        FROM Employee parent
        INNER JOIN chain c 
            ON parent.emp_id = c.report_to_hr
        WHERE parent.org_id = :orgID
          AND c.depth < :maxDepth
    )
    SELECT e.*
    FROM Employee e
    INNER JOIN chain c 
        ON e.emp_id = c.emp_id
    """, nativeQuery = true)
    List<EmployeeEntity> findReportingChainUpward(
            @Param("empID") Long empID,
            @Param("orgID") Long orgID,
            @Param("maxDepth") int maxDepth
    );

    @Query(value = """
    WITH RECURSIVE tree AS (
        SELECT 
            e.emp_id,
            e.org_id,
            e.report_to_hr,
            0 AS depth
        FROM Employee e
        WHERE e.report_to_hr = :managerId
          AND e.org_id = :orgID

        UNION ALL

        SELECT 
            child.emp_id,
            child.org_id,
            child.report_to_hr,
            t.depth + 1
        FROM Employee child
        INNER JOIN tree t 
            ON child.report_to_hr = t.emp_id
        WHERE child.org_id = :orgID
          AND t.depth < :maxDepth
    )
    SELECT e.*
    FROM Employee e
    INNER JOIN tree t 
        ON e.emp_id = t.emp_id
    """, nativeQuery = true)
    List<EmployeeEntity> findAllDescendants(
            @Param("managerId") Long managerId,
            @Param("orgID") Long orgID,
            @Param("maxDepth") int maxDepth
    );


    List<EmployeeEntity> findByOrganisation_OrgID(Long orgId);

    List<EmployeeEntity> findByOrganisation_OrgIDAndDepartment_DepartmentId(
            Long orgId, Long departmentId);
}