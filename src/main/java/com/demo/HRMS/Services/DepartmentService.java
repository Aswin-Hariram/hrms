package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.Department.CreateDepartmentRequest;
import com.demo.HRMS.Entities.DepartmentEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.DepartmentRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DepartmentService {


    private final OrganisationRepository org_repo;
    private final DepartmentRepository dep_repo;

    public DepartmentService(OrganisationRepository org_repo,DepartmentRepository dep_rep){
        this.org_repo = org_repo;
        this.dep_repo = dep_rep;
    }


    public Map<String,Object> CreateDepartment(CreateDepartmentRequest request){

        Long orgID = request.getOrgID();

        if(dep_repo.existsByDepartmentNameIgnoreCaseAndOrganisation_OrgID(request.getDepartmentName(),request.getOrgID())){
            throw new DataIntegrityViolationException("Deparment already exist.");
        }

        OrganisationEntity organisation = org_repo.findById(orgID).orElseThrow(() ->
                        new RuntimeException("Organisation not found with provided organisation ID")
        );
        DepartmentEntity newDepartment = new DepartmentEntity();
        newDepartment.setDepartmentName(request.getDepartmentName());
        newDepartment.setOrganisation(organisation);

        dep_repo.save(newDepartment);

        return Map.of("message","Created","data",newDepartment);



    }
}
