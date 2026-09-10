package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.EmployeesLeaveData.CreateLeaveSheetDTO;
import com.demo.HRMS.DTO.LeaveSheet.Response.LeaveSheetResponseDTO;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.LeaveSheetEntity;
import com.demo.HRMS.Entities.LeaveTypeEntity;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.LeaveSheetRepository;
import com.demo.HRMS.Repositories.LeaveTypeRepository;
import com.demo.HRMS.Repositories.OrganisationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LeaveSheetService {

    @Autowired
    private LeaveSheetRepository leaveSheetRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;



    public Map<String,Object> getLeaves(Long orgId){


        if(!organisationRepository.existsById(orgId)){
            throw new RuntimeException("Orgnaisation not found");
        }

        List<LeaveSheetEntity> leaves = leaveSheetRepository.findAllByOrganisation_OrgID(orgId);

        List<LeaveSheetResponseDTO> response = new ArrayList<>();
        for(LeaveSheetEntity leaveSheet : leaves){
            response.add(
                    LeaveSheetResponseDTO.builder()
                            .leaveID(leaveSheet.getLeaveType().getLeaveId())
                            .leaveName(leaveSheet.getLeaveType().getLeave_Name())
                            .allocatedDays(leaveSheet.getAllocatedDays())
                            .usedDays(leaveSheet.getUsedDays())
                            .remainingDays(leaveSheet.getRemainingDays())
                            .build()
            );
        }


        return Map.of(
                "message","success",
                "data",response
                );

    }


    @Transactional
    public Map<String,Object> createLeave(CreateLeaveSheetDTO request){

        EmployeeEntity employee = employeeRepository.findByEmpIDAndOrganisation_OrgID(
                request.getEmpID(),
                request.getOrgID()
        ).orElseThrow(()->
                new RuntimeException("Employee not found"));

        Long logged = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        if(!Objects.equals(employee.getOrganisation().getOrgID(), request.getOrgID())){
            throw new RuntimeException("You are not permitted to access this employee");
        }

        if(employee.getReportToHr()==null||!Objects.equals(employee.getReportToHr().getEmpID(), logged)){
            throw new RuntimeException("You are not permitted to access this employee");
        }



        for(Long leaveId: request.getLeaveIDs()){
            LeaveTypeEntity leaveType = leaveTypeRepository.findByOrganisation_OrgIDAndLeaveId(request.getOrgID(),leaveId).orElseThrow(()->
                    new RuntimeException("Invalid leave id"+ leaveId));

            LeaveSheetEntity sheetEntity = LeaveSheetEntity.builder()
                    .employee(employee)
                    .organisation(employee.getOrganisation())
                    .leaveType(leaveType)
                    .allocatedDays(leaveType.getNoDays())
                    .usedDays(0)
                    .remainingDays(leaveType.getNoDays())
                    .build();

           leaveSheetRepository.save(sheetEntity);

        }

        return Map.of(
          "message","success"
        );
    }



}
