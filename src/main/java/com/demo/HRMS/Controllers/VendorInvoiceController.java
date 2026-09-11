package com.demo.HRMS.Controllers;

import com.demo.HRMS.DTO.Vendor.CreateVendorInvoiceDTO;
import com.demo.HRMS.DTO.Vendor.VendorInvoiceResponse;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.VendorInvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor/invoices")
public class VendorInvoiceController {

    @Autowired private VendorInvoiceService invoiceService;
    @Autowired private JwtService jwtService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    public ResponseEntity<VendorInvoiceResponse> createInvoice(
            @Valid @RequestBody CreateVendorInvoiceDTO request,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);
        Long userId = jwtService.extractID(token);

        VendorInvoiceResponse response =
                invoiceService.createInvoice(request, orgId, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    public ResponseEntity<VendorInvoiceResponse> getInvoice(
            @PathVariable Long id,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        return ResponseEntity.ok(invoiceService.getInvoice(id, orgId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    public ResponseEntity<List<VendorInvoiceResponse>> listInvoices(
            @RequestParam(required = false) Long vendorId,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);

        return ResponseEntity.ok(invoiceService.listInvoices(orgId, vendorId));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    public ResponseEntity<VendorInvoiceResponse> submitForApproval(
            @PathVariable Long id,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);
        Long userId = jwtService.extractID(token);

        return ResponseEntity.ok(invoiceService.submitForApproval(id, orgId, userId));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    public ResponseEntity<VendorInvoiceResponse> approve(
            @PathVariable Long id,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);
        Long userId = jwtService.extractID(token);

        return ResponseEntity.ok(invoiceService.approve(id, orgId, userId));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('HR', 'SUPER_ADMIN') or hasAuthority('ACCOUNTANT')")
    public ResponseEntity<VendorInvoiceResponse> reject(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        String token = (String) authentication.getCredentials();
        Long orgId = jwtService.extractOrg(token);
        Long userId = jwtService.extractID(token);

        return ResponseEntity.ok(
                invoiceService.reject(id, orgId, userId, body.get("reason")));
    }
}