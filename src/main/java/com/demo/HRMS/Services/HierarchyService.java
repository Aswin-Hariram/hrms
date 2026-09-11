package com.demo.HRMS.Services;

import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HierarchyService {


    public static final int MAX_DEPTH = 5;

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
    public void validateReportingChain(Long employeeId, EmployeeEntity proposedManager) {
        if (proposedManager == null) return;


        if (employeeId != null
                && Objects.equals(employeeId, proposedManager.getEmpID())) {
            throw new RuntimeException("Employee cannot report to themselves");
        }

        int depth = 0;
        EmployeeEntity current = proposedManager;

        while (current != null) {
            depth++;
            if (depth > MAX_DEPTH) {
                throw new RuntimeException(
                        "Reporting chain exceeds maximum depth of " + MAX_DEPTH
                );
            }
            if (employeeId != null
                    && Objects.equals(current.getEmpID(), employeeId)) {
                throw new RuntimeException("Circular reporting chain detected");
            }

            current = current.getReportToHr();
        }
    }
}