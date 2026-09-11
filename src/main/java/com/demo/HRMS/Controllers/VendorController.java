package com.demo.HRMS.Controllers;


import com.demo.HRMS.DTO.Vendor.CreateVendorDTO;
import com.demo.HRMS.DTO.Vendor.CreateVendorResoponse;
import com.demo.HRMS.Security.JwtService;
import com.demo.HRMS.Services.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/vendor")
public class VendorController {


    @Autowired
    private VendorService vendorService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('HR','SUPER_ADMIN')")
    public ResponseEntity<CreateVendorResoponse> createVendor(
            @Valid @RequestBody CreateVendorDTO request, Authentication authentication) {

        String token = (String) authentication.getCredentials();

        Long orgId = jwtService.extractOrg(token);
        CreateVendorResoponse response = vendorService.createVendor(request,orgId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
