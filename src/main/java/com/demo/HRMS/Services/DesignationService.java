package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Designation.CreateDesignationRequest;
import com.demo.HRMS.Entities.DepartmentEntity;
import com.demo.HRMS.Entities.DesignationEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.DepartmentRepository;
import com.demo.HRMS.Repositories.DesignationRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class DesignationService {

    private final DepartmentRepository dep_repo;
    private final DesignationRepository desg_repo;
    private final OrganisationRepository org_repo;

    public DesignationService(
            DesignationRepository desg_repo,
            DepartmentRepository dep_repo,
            OrganisationRepository org_repo
    ) {
        this.desg_repo = desg_repo;
        this.dep_repo = dep_repo;
        this.org_repo = org_repo;
    }

    @Transactional
    public Map<String, Object> createDesignation(CreateDesignationRequest request) {

        DepartmentEntity department = dep_repo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        OrganisationEntity organisation = org_repo.findById(request.getOrgID())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        if (!Objects.equals(
                department.getOrganisation().getOrgID(),
                request.getOrgID()
        )) {
            throw new RuntimeException("Department does not belong to this organisation");
        }

        if (desg_repo
                .existsByDesignationNameIgnoreCaseAndOrganisation_OrgIDAndDepartment_DepartmentId(
                        request.getDesignationName(),
                        request.getOrgID(),
                        request.getDepartmentId()
                )) {
            throw new DataIntegrityViolationException("Designation already exists.");
        }

        DesignationEntity newDesignation = new DesignationEntity();
        newDesignation.setDepartment(department);
        newDesignation.setOrganisation(organisation);
        newDesignation.setDesignationName(request.getDesignationName());

        desg_repo.save(newDesignation);

        return Map.of(
                "Message", "Created",
                "data", newDesignation
        );
    }
}