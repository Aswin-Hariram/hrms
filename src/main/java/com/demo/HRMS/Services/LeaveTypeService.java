package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.LeaveType.CreateLeaveTypeDTO;
import com.demo.HRMS.DTO.LeaveType.Response.GetALL_LeaveTypeResponseDTO;
import com.demo.HRMS.Entities.LeaveTypeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.LeaveTypesCodes;
import com.demo.HRMS.Repositories.LeaveTypeRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class LeaveTypeService {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;
    @Autowired
    private OrganisationRepository organisationRepository;

    public Map<String,Object> createLeaveType(CreateLeaveTypeDTO request){


        OrganisationEntity organisation = organisationRepository.findById(request.getOrgID()).orElseThrow(()->
           new RuntimeException("Organisation not found.")
        );


        request.setLeaveCode(request.getLeaveCode().toUpperCase());
        if (Arrays.stream(LeaveTypesCodes.values())
                .noneMatch(code -> code.name().equalsIgnoreCase(request.getLeaveCode()))) {

            throw new RuntimeException("Invalid leave code");
        }

            LeaveTypeEntity leaveType = LeaveTypeEntity.builder()
               .organisation(organisation).
               leave_Name(request.getLeaveName())
               .leaveCode(LeaveTypesCodes.valueOf(request.getLeaveCode()))
               .isPaid(request.getIspaid())
               .isApprovalRequired(request.getApprovalRequired())
               .isActive(request.getIsActive()).noDays(request.getNoDays())

                       .build();

       LeaveTypeEntity savedLeaveEntity = leaveTypeRepository.save(leaveType);






        return Map.of(
                "Message","Success",
                "data",savedLeaveEntity
        );
    }


    public Map<String, Object> getAllLeaveTypes(Long orgId) {

        if (!organisationRepository.existsById(orgId)) {
            throw new RuntimeException("Organisation not found");
        }


        List<LeaveTypeEntity> allLeaveTypes =
                leaveTypeRepository.findByOrganisation_OrgID(orgId);

        List<GetALL_LeaveTypeResponseDTO> response = new ArrayList<>();


        for (LeaveTypeEntity leaveType : allLeaveTypes) {
            response.add(new GetALL_LeaveTypeResponseDTO(leaveType));
        }

        return Map.of(
                "message", "success",
                "data", response
        );

    }
}
