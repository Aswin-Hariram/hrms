package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.LeaveRequest.Response.PendingLeaveResponse;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.LeaveRequestEntity;
import com.demo.HRMS.Entities.LeaveSheetEntity;
import com.demo.HRMS.Types.LeaveRequestStatus;
import com.demo.HRMS.Repositories.EmployeeRepository;
import com.demo.HRMS.Repositories.LeaveRequestRepository;
import com.demo.HRMS.Repositories.LeaveSheetRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LeaveRequestService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveSheetRepository leaveSheetRepository;


    @Transactional
    public Map<String,Object> getAllLeaveRequests(Long empId, Long orgId){

        EmployeeEntity loggedEmp = employeeRepository.findByEmpIDAndOrganisation_OrgID(empId,orgId)
                .orElseThrow(()->new RuntimeException("User not found"));


        List<LeaveRequestEntity> pending = leaveRequestRepository.findAllByOrganisation_OrgIDAndRequestedToAndStatus(
                orgId,
                loggedEmp,
                LeaveRequestStatus.SUBMITTED
        );

        List<PendingLeaveResponse> response = pending.stream()
                .map(request -> PendingLeaveResponse.builder()

                        .requestID(request.getRequestID())
                        .employeeID(request.getEmployee().getEmpID())
                        .employeeName(request.getEmployee().getEmpFirstName() + " " + request.getEmployee().getEmpLastName()
                        )
                        .employeeEmail(request.getEmployee().getEmpEmail())
                        .leaveID(request.getLeaveSheet().getEmpLeaveId())
                        .noOfDays(request.getNoOfDays())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .leaveName(request.getLeaveSheet().getLeaveType().getLeave_Name())
                        .status(request.getStatus())
                        .reason(request.getReason())
                        .requestedToID(request.getRequestedTo().getEmpID())
                        .requestedToName(request.getRequestedTo().getEmpFirstName() + " " + request.getRequestedTo().getEmpLastName())
                        .requestedAt(request.getRequestedAt())
                        .updatedAt(request.getUpdatedAt())
                        .build()
                )
                .toList();






        return Map.of(
                "message","success",
                "data",response
        );
    }


    @Transactional
    public Map<String,Object> approveLeave(Long requestId,Long hrId,Long orgId){

        EmployeeEntity hr = employeeRepository.findByEmpIDAndOrganisation_OrgID(hrId,orgId)
                .orElseThrow(()->new RuntimeException("User not found"));


        LeaveRequestEntity request = leaveRequestRepository.findById(requestId).orElseThrow(
                () -> new RuntimeException("Leave Request not found")
        );

        if(request.getRequestedTo() != hr ){
            throw new RuntimeException("You dont have access to this request");
        }


        if(!request.getStatus().equals(LeaveRequestStatus.SUBMITTED)){
            throw new RuntimeException("Only submitted status leaves can be approved");
        }

        request.setStatus(LeaveRequestStatus.APPROVED);

        leaveRequestRepository.save(request);


        LeaveSheetEntity leaveSheet = leaveSheetRepository.findByLeaveType_LeaveCodeAndOrganisation_OrgIDAndEmployee_EmpID(
                request.getLeaveSheet().getLeaveType().getLeaveCode(),
                orgId,
                request.getEmployee().getEmpID()
        ).orElseThrow(()->new RuntimeException("Leave not found"));


        leaveSheet.setUsedDays(leaveSheet.getUsedDays()+request.getNoOfDays());
        leaveSheet.setRemainingDays(leaveSheet.getRemainingDays()-request.getNoOfDays());



        leaveSheetRepository.save(leaveSheet);


        return Map.of(
                "message","Leave approved"
        );
    }
}
