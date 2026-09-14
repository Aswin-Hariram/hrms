package com.demo.HRMS.Services;

import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HierarchyService {


    public static final int MAX_DEPTH = 3;

    private final EmployeeRepository employeeRepository;

    public HierarchyService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }



    @Transactional(readOnly = true)
    public Set<Long> getAncestorIds(Long empID, Long orgID) {
        return employeeRepository
                .findReportingChainUpward(empID, orgID, MAX_DEPTH)
                .stream()
                .map(EmployeeEntity::getEmpID)
                .collect(Collectors.toSet());
    }


    @Transactional(readOnly = true)
    public Set<Long> getDescendantIds(Long managerId, Long orgID) {
        return employeeRepository
                .findAllDescendants(managerId, orgID, MAX_DEPTH)
                .stream()
                .map(EmployeeEntity::getEmpID)
                .collect(Collectors.toSet());
    }


    @Transactional(readOnly = true)
    public boolean isManagerOf(Long managerId, Long employeeId, Long orgID) {
        if (managerId == null || employeeId == null) return false;
        if (Objects.equals(managerId, employeeId)) return false;
        return getAncestorIds(employeeId, orgID).contains(managerId);
    }


    @Transactional(readOnly = true)
    public boolean isDirectManagerOf(Long managerId, EmployeeEntity employee) {
        return employee.getReportToHr() != null
                && Objects.equals(employee.getReportToHr().getEmpID(), managerId);
    }


    @Transactional(readOnly = true)
    public void validateReportingChain(
            Long employeeId,
            EmployeeEntity proposedManager
    ) {
        if (proposedManager == null) {
            return;
        }

        // Self-reporting check
        if (employeeId != null
                && Objects.equals(employeeId, proposedManager.getEmpID())) {

            throw new IllegalArgumentException(
                    "Employee cannot report to themselves"
            );
        }

        Set<Long> visited = new HashSet<>();

        EmployeeEntity current = proposedManager;
        int depth = 0;

        while (current != null) {

            depth++;

            if (depth > MAX_DEPTH) {
                throw new IllegalArgumentException(
                        "Reporting chain exceeds maximum depth of " + MAX_DEPTH
                );
            }

            Long currentEmployeeId = current.getEmpID();


            if (currentEmployeeId != null
                    && !visited.add(currentEmployeeId)) {

                throw new IllegalArgumentException(
                        "Circular reporting chain detected"
                );
            }

            if (employeeId != null
                    && Objects.equals(currentEmployeeId, employeeId)) {

                throw new IllegalArgumentException(
                        "Circular reporting chain detected"
                );
            }

            current = current.getReportToHr();
        }
    }
}