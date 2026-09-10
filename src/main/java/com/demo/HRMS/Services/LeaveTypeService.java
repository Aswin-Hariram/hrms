package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.LeaveType.CreateLeaveTypeDTO;
import com.demo.HRMS.DTO.LeaveType.Response.GetALL_LeaveTypeResponseDTO;
import com.demo.HRMS.Entities.LeaveTypeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.LeaveTypeRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import com.demo.HRMS.Types.LeaveTypesCodes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public Map<String, Object> createLeaveType(CreateLeaveTypeDTO request) {

        OrganisationEntity organisation = organisationRepository
                .findById(request.getOrgID())
                .orElseThrow(() -> new RuntimeException("Organisation not found."));

        if (request.getNoDays() <= 0) {
            throw new RuntimeException("Number of days must be greater than 0");
        }

        String codeStr = request.getLeaveCode().toUpperCase();
        if (Arrays.stream(LeaveTypesCodes.values())
                .noneMatch(code -> code.name().equalsIgnoreCase(codeStr))) {
            throw new RuntimeException("Invalid leave code");
        }

        if (leaveTypeRepository.existsByOrganisation_OrgIDAndLeaveNameIgnoreCase(
                request.getOrgID(),
                request.getLeaveName()
        )) {
            throw new RuntimeException("Leave type with this name already exists");
        }

        LeaveTypeEntity leaveType = LeaveTypeEntity.builder()
                .organisation(organisation)
                .leaveName(request.getLeaveName())
                .leaveCode(LeaveTypesCodes.valueOf(codeStr))
                .isPaid(request.getIspaid())
                .isApprovalRequired(request.getApprovalRequired())
                .isActive(request.getIsActive())
                .noDays(request.getNoDays())
                .build();

        LeaveTypeEntity saved = leaveTypeRepository.save(leaveType);

        return Map.of(
                "Message", "Success",
                "data", saved
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