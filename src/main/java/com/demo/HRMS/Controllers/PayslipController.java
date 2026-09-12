package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Payslip.*;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.PayslipService;
import com.demo.HRMS.Types.PayslipStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payslip")
public class PayslipController {

    @Autowired private PayslipService payslipService;
    @Autowired private JwtService jwtService;

    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    @PostMapping("/employee/{empId}")
    public ResponseEntity<?> generateForEmployee(
            @PathVariable Long empId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        LocalDate end = periodEnd != null ? periodEnd : LocalDate.now();
        PayslipResponse response =
                payslipService.generateForEmployee(orgId, empId, periodStart, end);

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    @PostMapping("/bulk")
    public ResponseEntity<?> generateBulk(
            @Valid @RequestBody BulkPayslipRequest request,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        request.setOrgId(orgId);

        BulkPayslipResponse response = payslipService.generateBulk(request);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getForEmployee(
            @PathVariable Long empId,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        List<PayslipResponse> response =
                payslipService.getPayslipsForEmployee(orgId, empId);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','SUPER_ADMIN')")
    @GetMapping("/myPayslipHistory")
    public ResponseEntity<?> getMypaySlip(Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);
        Long empId = jwtService.extractID(token);

        List<PayslipResponse> response =
                payslipService.getPayslipsForEmployee(orgId, empId);
        return ResponseEntity.ok(response);
    }



    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    @GetMapping("/{payslipId}")
    public ResponseEntity<?> getPayslip(
            @PathVariable Long payslipId,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        return ResponseEntity.ok(payslipService.getPayslip(orgId, payslipId));
    }



    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    @PostMapping("/approve")
    public ResponseEntity<?> approve(
            @Valid @RequestBody ApproveRequest request,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId    = jwtService.extractOrg(token);
        Long callerId = jwtService.extractID(token);

        return ResponseEntity.ok(payslipService.approve(request, callerId, orgId));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    @PostMapping("/pay")
    public ResponseEntity<?> pay(
            @Valid @RequestBody PayRequest request,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId    = jwtService.extractOrg(token);
        Long callerId = jwtService.extractID(token);

        return ResponseEntity.ok(payslipService.pay(request, callerId, orgId));
    }

    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    @GetMapping("/list")
    public ResponseEntity<?> listByStatus(
            @RequestParam PayslipStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        return ResponseEntity.ok(
                payslipService.listByStatus(orgId, status, periodFrom, periodTo));
    }

    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    @GetMapping("/getAllPayslip")
    public ResponseEntity<?> getAllPayslip(
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        return ResponseEntity.ok(
                payslipService.getAllPayslip(orgId));
    }
}