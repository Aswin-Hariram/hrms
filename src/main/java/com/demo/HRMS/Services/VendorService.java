package com.demo.HRMS.Services;


import com.demo.HRMS.DTO.Vendor.CreateVendorDTO;
import com.demo.HRMS.DTO.Vendor.CreateVendorResoponse;

import com.demo.HRMS.Entities.OrganisationEntity;
import com.demo.HRMS.Entities.VendorEntity;
import com.demo.HRMS.Repositories.OrganisationRepository;
import com.demo.HRMS.Repositories.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendorService {

    @Autowired
    private  VendorRepository vendorRepository;

    @Autowired
    private OrganisationRepository organisationRepository;
    @Transactional
    public CreateVendorResoponse createVendor(CreateVendorDTO request, Long orgId) {

        if (vendorRepository.existsByOrganisation_OrgIDAndEmail(orgId,request.getEmail())) {
            throw new RuntimeException("Vendor with this email already exists");
        }

        if (vendorRepository.existsByOrganisation_OrgIDAndVendorName(orgId,request.getVendorName())) {
            throw new RuntimeException("Vendor with this name already exists");
        }

        if (request.getGstNumber() != null
                && vendorRepository.existsByOrganisation_OrgIDAndGstNumber(orgId,request.getGstNumber())) {
            throw new RuntimeException("Vendor with this GST number already exists");
        }

        OrganisationEntity org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        VendorEntity vendor = VendorEntity.builder()
                .vendorName(request.getVendorName())
                .email(request.getEmail())
                .organisation(org)
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .contactPerson(request.getContactPerson())
                .gstNumber(request.getGstNumber())
                .isActive(true)
                .build();

        VendorEntity savedVendor = vendorRepository.save(vendor);

        return CreateVendorResoponse.builder()
                .id(savedVendor.getId())
                .vendorName(savedVendor.getVendorName())
                .email(savedVendor.getEmail())
                .phoneNumber(savedVendor.getPhoneNumber())
                .address(savedVendor.getAddress())
                .contactPerson(savedVendor.getContactPerson())
                .gstNumber(savedVendor.getGstNumber())
                .active(savedVendor.getIsActive())
                .message("Vendor created successfully")
                .build();
    }
}