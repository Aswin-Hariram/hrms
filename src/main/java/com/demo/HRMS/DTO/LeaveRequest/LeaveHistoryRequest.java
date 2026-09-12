package com.demo.HRMS.DTO.LeaveRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Data
public class LeaveHistoryRequest{

        @NotNull
        Long empId;

        @NotNull
        LocalDate fromDate;

        @NotNull
        LocalDate endDate;

}