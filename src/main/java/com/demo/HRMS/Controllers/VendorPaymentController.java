package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Vendor.CreateVendorPaymentDTO;
import com.demo.HRMS.DTO.Vendor.VendorPaymentResponse;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.VendorPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/payments")
public class VendorPaymentController {

    @Autowired private VendorPaymentService paymentService;
    @Autowired private JwtService jwtService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    public ResponseEntity<VendorPaymentResponse> createPayment(
            @Valid @RequestBody CreateVendorPaymentDTO request,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);
        Long userId = jwtService.extractID(token);

        VendorPaymentResponse response =
                paymentService.createPayment(request, orgId, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    public ResponseEntity<List<VendorPaymentResponse>> getPayments(
            @PathVariable Long invoiceId,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        return ResponseEntity.ok(paymentService.getPaymentsForInvoice(invoiceId, orgId));
    }
}