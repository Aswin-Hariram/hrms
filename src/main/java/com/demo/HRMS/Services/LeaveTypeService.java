package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.LeaveType.CreateLeaveTypeDTO;
import com.demo.HRMS.DTO.LeaveType.Response.GetALL_LeaveTypeResponseDTO;
import com.demo.HRMS.Entities.LeaveTypeEntity;
import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Repositories.LeaveTypeRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class LeaveTypeService {


    private static final Pattern CODE_PATTERN =
            Pattern.compile("^[A-Z][A-Z0-9_]{1,19}$");

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

        String code = normalizeAndValidateCode(request.getLeaveCode());

        String name = request.getLeaveName() == null
                ? null
                : request.getLeaveName().trim();

        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Leave name is required");
        }

        if (leaveTypeRepository.existsByOrganisation_OrgIDAndLeaveNameIgnoreCase(
                request.getOrgID(), name)) {
            throw new RuntimeException("Leave type with this name already exists");
        }

        if (leaveTypeRepository.existsByOrganisation_OrgIDAndLeaveCodeIgnoreCase(
                request.getOrgID(), code)) {
            throw new RuntimeException("Leave type with this code already exists");
        }

        LeaveTypeEntity leaveType = LeaveTypeEntity.builder()
                .organisation(organisation)
                .leaveName(name)
                .leaveCode(code)
                .isPaid(request.getIspaid())
                .isApprovalRequired(request.getApprovalRequired())
                .isActive(request.getIsActive())
                .noDays(request.getNoDays())
                .build();

        try {
            LeaveTypeEntity saved = leaveTypeRepository.saveAndFlush(leaveType);

            return Map.of(
                    "Message", "Success",
                    "data", saved
            );
        } catch (DataIntegrityViolationException e) {

            throw new RuntimeException(
                    "Leave type with this name or code already exists");
        }
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

    private String normalizeAndValidateCode(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("Leave code is required");
        }
        String code = raw.trim().toUpperCase().replaceAll("\\s+", "_");

        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new RuntimeException(
                    "Invalid leave code. Use 2-20 chars: A-Z, 0-9, underscore; "
                            + "must start with a letter.");
        }
        return code;
    }
}