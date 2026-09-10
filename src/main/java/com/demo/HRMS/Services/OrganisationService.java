package com.demo.HRMS.Services;

import com.demo.HRMS.Entities.DepartmentEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.DepartmentRepository;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import com.demo.HRMS.Types.EmployeeRole;
import com.demo.HRMS.Types.EmploymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
public class OrganisationService {

    @Autowired
    private OrganisationRepository org_repo;

    @Autowired
    private DepartmentRepository dep_repo;

    @Autowired
    private EmployeeRepository emp_repo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Map<String, Object> register(OrganisationEntity organisation) {

        if (org_repo.existsByOrgEmail(organisation.getOrgEmail())) {
            throw new DataIntegrityViolationException(
                    "Organisation with this email already exists"
            );
        }

        organisation.setOrgStatus("NOT ACTIVE");
        org_repo.save(organisation);

        EmployeeEntity superAdmin = new EmployeeEntity();
        superAdmin.setOrganisation(organisation);
        superAdmin.setEmpFirstName("System");
        superAdmin.setEmpLastName("Admin");
        superAdmin.setEmpEmail(organisation.getOrgEmail());
        superAdmin.setEmpPassword(
                passwordEncoder.encode("admin@" + organisation.getOrgPhone())
        );
        superAdmin.setEmpPhoneNumber(organisation.getOrgPhone());
        superAdmin.setEmpDOB(LocalDate.of(2000, 1, 1));
        superAdmin.setAge(26);
        superAdmin.setEmpJoiningDate(LocalDate.now());
        superAdmin.setEmpType(EmploymentType.FULL_TIME);
        superAdmin.setEmpStatus("INACTIVE");
        superAdmin.setEmpRole(EmployeeRole.SUPER_ADMIN);
        superAdmin.setDefaultPasswordUpdated(false);

        emp_repo.save(superAdmin);

        return Map.of(
                "Status", "Successful",
                "org_details", organisation,
                "admin_details", Map.of(
                        "email", superAdmin.getEmpEmail(),
                        "role", superAdmin.getEmpRole(),
                        "status", superAdmin.getEmpStatus(),
                        "DefaultPass", "admin@" + organisation.getOrgPhone()
                )
        );
    }

    @Transactional
    public ResponseEntity<?> createDepartment(DepartmentEntity request) {

        if (request.getOrganisation() == null
                || request.getOrganisation().getOrgID() == null) {
            throw new RuntimeException("Organisation is required");
        }

        Long orgId = request.getOrganisation().getOrgID();

        if (!org_repo.existsById(orgId)) {
            throw new RuntimeException("Organisation not found");
        }

        if (dep_repo.existsByDepartmentNameIgnoreCaseAndOrganisation_OrgID(
                request.getDepartmentName(),
                orgId
        )) {
            throw new DataIntegrityViolationException("Department already exists");
        }

        DepartmentEntity savedDepartment = dep_repo.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Successful",
                        "departmentID", savedDepartment.getDepartmentId()
                )
        );
    }
}